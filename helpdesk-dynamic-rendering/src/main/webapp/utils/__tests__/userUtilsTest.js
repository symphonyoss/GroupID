const userUtils = require('../userUtils');

const extendedUserService = {
    getUserId: jest.fn(),
    getRooms: jest.fn(),
};

const subscribe = jest.fn().mockImplementation((serviceName) => {
    if(serviceName === 'extended-user-service') {
        return extendedUserService;
    }
});

global.SYMPHONY = {
    services: {
        subscribe: subscribe
    }
};

beforeEach(() => {
    subscribe.mockClear();
    extendedUserService.getRooms.mockClear();
    extendedUserService.getUserId.mockClear();
});

describe('User Utils', () => {
    it('Should subscribe to SYMPHONY.services.\'extended-user-service\'', () => {
        userUtils.getUserId();
        userUtils.getRooms();
        expect(subscribe.mock.calls.length).toBe(2);
    });
    it('Should call getUserId from SYMPHONY.services.\'extended-user-service\'', () => {
        userUtils.getUserId();
        expect(extendedUserService.getUserId.mock.calls.length).toBe(1);
    });
    it('Should call getRooms from SYMPHONY.services.\'extended-user-service\'', () => {
        userUtils.getRooms();
        expect(extendedUserService.getRooms.mock.calls.length).toBe(1);
    });
});