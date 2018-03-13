export const getUserId = () => {
  const extendedUserService = SYMPHONY.services.subscribe('extended-user-service');
  return extendedUserService.getUserId();
};

export const getRooms = () => {
  const extendedUserService = SYMPHONY.services.subscribe('extended-user-service');
  return extendedUserService.getRooms();
};
