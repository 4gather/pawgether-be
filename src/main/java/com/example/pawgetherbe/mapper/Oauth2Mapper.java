package com.example.pawgetherbe.mapper;

import com.example.pawgetherbe.common.filter.dto.OauthDto.oauth2SignUpRequest;
import com.example.pawgetherbe.domain.entity.OauthEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface Oauth2Mapper {
    OauthEntity toEntity(oauth2SignUpRequest oauth2SignUpRequest);
}
