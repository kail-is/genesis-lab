package com.codingtest.genesislab.web.user;

import com.codingtest.genesislab.domain.User;
import com.codingtest.genesislab.web.user.out.UserCurrentInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "role", target = "role")
    UserCurrentInfoDto toCurrentInfoDto(User user);

    List<UserCurrentInfoDto> toCurrentInfoDtoList(List<User> users);
}