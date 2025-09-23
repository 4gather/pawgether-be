package com.example.pawgetherbe.service.command;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto;
import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto.PetFairCreateRequest;
import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto.PetFairCreateResponse;
import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto.UpdatePetFairRequest;
import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto.UpdatePetFairResponse;
import com.example.pawgetherbe.domain.UserContext;
import com.example.pawgetherbe.domain.entity.PetFairEntity;
import com.example.pawgetherbe.domain.entity.PetFairImageEntity;
import com.example.pawgetherbe.domain.status.PetFairStatus;
import com.example.pawgetherbe.domain.status.UserRole;
import com.example.pawgetherbe.mapper.command.PetFairCommandMapper;
import com.example.pawgetherbe.repository.command.PetFairCommandRepository;
import com.example.pawgetherbe.repository.command.UserCommandRepository;
import com.example.pawgetherbe.usecase.post.DeletePostUseCase;
import com.example.pawgetherbe.usecase.post.EditPostUseCase;
import com.example.pawgetherbe.usecase.post.RegistryPostUseCase;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import static com.example.pawgetherbe.domain.UserContext.getUserId;
import static com.example.pawgetherbe.exception.command.PetFairCommandErrorCode.*;
import static com.example.pawgetherbe.exception.command.UserCommandErrorCode.NOT_FOUND_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetFairCommandService implements RegistryPostUseCase, DeletePostUseCase, EditPostUseCase {

    private final PetFairCommandRepository petFairCommandRepository;
    private final UserCommandRepository userCommandRepository;
    private final PetFairCommandMapper petFairCommandMapper;

    private final S3Client r2Client;

    private final String bucketName = "pawgether-public";

    @Override
    @Transactional
    public PetFairCreateResponse postCreate(PetFairCreateRequest req) {
//        var id = Long.valueOf(getUserId());
        // TODO: 실제 서비스에서는 Long.valueOf(getUserId()) 사용
        var id = 1L;

        var user = userCommandRepository.findById(id).orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        try {
            var date = LocalDate.parse(req.startDate());
            List<PetFairImageEntity> petFairImageEntities = new ArrayList<>();

            String baseName = String.format("%02d%02d", date.getMonthValue(), date.getDayOfMonth());

            // 포스터 이미지 업로드
            String posterKey = String.format("poster/%d/%02d/%s.webp",
                    date.getYear(), date.getMonthValue(), baseName);
            byte[] posterBytes = toWebp(req.posterImage());
//            uploadToR2(posterKey, posterBytes);

            // 추가 이미지 업로드 (parallel 변환)
            List<byte[]> images = toWebpsParallel(req.images());
            for (int i = 0; i < images.size(); i++) {
                String key = String.format("images/%d/%02d/%s-%d.webp",
                        date.getYear(), date.getMonthValue(), baseName, (i + 1));

                var builder = PetFairImageEntity.builder()
                        .imageUrl(key)
                        .sortOrder((i + 1))
                        .build();

                petFairImageEntities.add(builder);
//                uploadToR2(key, images.get(i));
            }

            var PetFairEntity = petFairCommandMapper.toPetFairEntity(req);
            PetFairEntity.updateImage(posterKey, petFairImageEntities, PetFairStatus.ACTIVE, user);

            var petFair = petFairCommandRepository.save(PetFairEntity);
            return petFairCommandMapper.toPetFairCreateResponse(petFair);

        } catch (CustomException ce) {
            throw ce;
        } catch (Exception e) {
            throw new CustomException(PET_FAIR_CREATE_FAIL);
        }
    }

    @Override
    @Transactional
    public void deletePost(long postId) {
        var post = petFairCommandRepository.findById(postId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_PET_FAIR));
        if(post.getStatus() == PetFairStatus.REMOVED) {
            throw new CustomException(REMOVED_PET_FAIR);
        }
        post.updateStatus(PetFairStatus.REMOVED);
    }

//    private void uploadToR2(String key, byte[] data) {
//        r2Client.putObject(
//                PutObjectRequest.builder()
//                        .bucket(bucketName)
//                        .key(key)
//                        .contentType("image/webp")
//                        .build(),
//                RequestBody.fromBytes(data)
//        );
//        log.info("업로드 완료: {}", key);
//    }


    @Override
    @Transactional
    public UpdatePetFairResponse updatePetFairPost(Long petFairId, UpdatePetFairRequest updatePetFairRequest) {

        PetFairEntity entity = petFairCommandRepository.findById(petFairId).orElseThrow(
                () -> new CustomException(NOT_FOUND_PET_FAIR)
        );

        if (entity.getStatus() == PetFairStatus.REMOVED) {
            throw new CustomException(REMOVED_PET_FAIR);
        }

//         현재는 ADMIN만 등록/수정하는 정책 - UserContext 이용해서 Role 획득하여 검증
//        if (!UserContext.getUserRole().equals(UserRole.ADMIN.toString())) {
//            throw new CustomException(UNAUTHORIZED_UPDATE_PET_FAIR);
//        }

        try {
            var date = LocalDate.parse(updatePetFairRequest.startDate());
            List<PetFairImageEntity> petFairImageEntities = new ArrayList<>();

            String baseName = String.format("%02d%02d", date.getMonthValue(), date.getDayOfMonth());

            // 포스터 이미지 업로드
            String posterKey = String.format("poster/%d/%02d/%s.webp",
                    date.getYear(), date.getMonthValue(), baseName);
            byte[] posterBytes = toWebp(updatePetFairRequest.posterImage());
//            uploadToR2(posterKey, posterBytes);

            // 추가 이미지 업로드 (parallel 변환)
            List<byte[]> images = toWebpsParallel(updatePetFairRequest.images());
            for (int i = 0; i < images.size(); i++) {
                String key = String.format("images/%d/%02d/%s-%d.webp",
                        date.getYear(), date.getMonthValue(), baseName, (i + 1));

                var builder = PetFairImageEntity.builder()
                        .imageUrl(key)
                        .sortOrder((i + 1))
                        .build();

                petFairImageEntities.add(builder);
//                uploadToR2(key, images.get(i));
            }

            entity.updatePetFair(posterKey, petFairImageEntities, updatePetFairRequest);

        } catch (CustomException ce) {
            throw ce;
        } catch (Exception e) {
            throw new CustomException(PET_FAIR_UPDATE_FAIL);
        }

        return petFairCommandMapper.toUpdatePetFairResponse(entity);
    }

    private byte[] toWebp(MultipartFile file) {
        try {
            String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
            String ct   = file.getContentType() == null ? "" : file.getContentType().toLowerCase();

            if (ct.startsWith("image/webp") || name.endsWith(".webp")) {
                return file.getBytes();
            }

            ImmutableImage img = ImmutableImage.loader().fromStream(file.getInputStream());
            return img.bytes(WebpWriter.DEFAULT);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(IMAGE_CONVERT_FAIL);
        }
    }

    private List<byte[]> toWebpsParallel(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return List.of();

        try(var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = files.stream()
                    .map(f -> CompletableFuture.supplyAsync(() -> toWebp(f), exec))
                    .toList();

            // 입력 순서 그대로 결과 생성
            return futures.stream().map(CompletableFuture::join).toList();
        }
    }
}
