package org.ylzl.eden.uaa.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;
import org.ylzl.eden.uaa.domain.User;
import org.ylzl.eden.uaa.service.dto.UserDTO;
import org.ylzl.eden.uaa.web.rest.vm.UserVM;

import java.util.List;

/**
 * 用户模型映射器
 *
 * @author gyl
 * @since 1.0.0
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  UserDTO userToUserDTO(User user);

  List<UserDTO> usersToUserDTOs(List<User> users);

  User userDTOToUser(UserDTO userDTO);

  List<User> userDTOsToUsers(List<UserDTO> userDTOs);

  void updateUserFromUserDTO(UserDTO userDTO, @MappingTarget User user);

  void updateUserDTOFromUser(User user, @MappingTarget UserDTO userDTO);

  UserVM userToUserVM(User user);

  List<UserVM> usersToUserVMs(List<User> users);
}
