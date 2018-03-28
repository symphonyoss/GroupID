import actionFactory from "../actionFactory";

const mockAction1 = {
    id: 1,
    type: 'mockAction1',
    label: 'mockAction1Label',
    service: 'mockActionService',
    enricherInstanceId: 'mockActionEnricher',
    show: true,
    userName: 'mockUserName',
    userId: 1
};

const mockAction2 = {
    type: 'mockAction2',
    label: 'mockAction2Label',
    service: 'mockActionService',
    enricherInstanceId: 'mockActionEnricher',
    show: false,
    userName: 'mockUserName',
    userId: 2
};

const mockService = {};

const mockEntity = {};

const expectedBuiltResult = {
    '1': {
        service: mockService,
        label: mockAction1.label,
        data: {
            entity: mockEntity,
            service: mockAction1.service,
            type: mockAction1.type,
            enricherInstanceId: mockAction1.enricherInstanceId,
            show: mockAction1.show,
            userName: mockAction1.userName,
            userId: mockAction1.userId
        }
    },
    'mockAction2': {
        service: mockService,
        label: mockAction2.label,
        data: {
            entity: mockEntity,
            service: mockAction2.service,
            type: mockAction2.type,
            enricherInstanceId: mockAction2.enricherInstanceId,
            show: mockAction2.show,
            userName: mockAction2.userName,
            userId: mockAction2.userId
        }
    }
};

describe('Action Factory', () => {
    it('Should build actions as expected:', () => {
        const result = actionFactory([mockAction1, mockAction2], mockService, mockEntity);
        expect(result).toEqual(expectedBuiltResult);
    });
});