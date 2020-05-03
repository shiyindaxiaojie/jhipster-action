package org.ylzl.eden.uaa.service.mapstruct;

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
 * @since 0.0.1
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapstruct {

  UserMapstruct INSTANCE = Mappers.getMapper(UserMapstruct.class);

  UserDTO userToUserDTO(User user);

  List<UserDTO> usersToUserDTOs(List<User> users);

  User userDTOToUser(UserDTO userDTO);

  List<User> userDTOsToUsers(List<UserDTO> userDTOs);

  void updateUserFromUserDTO(UserDTO userDTO, @MappingTarget User user);

  void updateUserDTOFromUser(User user, @MappingTarget UserDTO userDTO);

  UserVM userToUserVM(User user);

  List<UserVM> usersToUserVMs(List<User> users);
}
