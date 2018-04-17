import MessageEnricher from '../messageEnricher';
import { getUserId, getRooms } from '../../utils/userUtils';
import actionFactory from '../../utils/actionFactory';
import { renderErrorMessage } from '../../utils/errorMessage';
import claimTicketActions from '../../templates/claimTicketActions.hbs';
const base64 = require('base64-url');

import ActionClaimTicketEnricher from '../actionClaimTicketEnricher';

jest.mock('../messageEnricher');
jest.mock('../../utils/userUtils');
jest.mock('../../utils/actionFactory');
jest.mock('../../utils/errorMessage');
jest.mock('../../templates/claimTicketActions.hbs');
jest.mock('base64-url');

const entityRegistry = {
    updateEnricher: jest.fn()
};

const subscribe = jest.fn().mockImplementation((serviceName) => {
    if(serviceName === 'entity') {
        return entityRegistry;
    }
});

global.SYMPHONY = {
    services: {
        subscribe: subscribe
    }
};

const mockInRoomList = [{ threadId: 'krjijasd///12039==1jdfja23' }];

const mockType = 'mockType';

const mockEntity1 = {
    state: 'UNRESOLVED',
    ticketId: 'A3M5F9K3S0',
    streamId: 'krjijasd___12039__1jdfja23',
    agent: {
        displayName: 'Tester'
    }
};

const mockEntityNoAgent = {
    state: 'UNRESOLVED',
    ticketId: 'A3M5F9K3S0',
    streamId: 'krjijasd___12039__1jdfja23'
};

const mockEntityUnserviced = {
    state: 'UNSERVICED',
    ticketId: 'A3M5F9K3S0',
    streamId: 'krjijasd___12039__1jdfja23',
    agent: {
        displayName: 'Tester'
    }
};

const mockActionData = 'mockActionData';
const mockTemplate = 'mockTemplate';

const expectedActions = [{ id: 'joinConversation',
    service: 'helpdesk-enricher',
    type: 'joinConversation',
    label: 'Join the conversation',
    enricherInstanceId: 'A3M5F9K3S0',
    show: true,
    userName: 'Tester',
    streamId: 'krjijasd___12039__1jdfja23',
    userId: 123456789
}];

const expectedActionsNoUsername = [{ id: 'joinConversation',
    service: 'helpdesk-enricher',
    type: 'joinConversation',
    label: 'Join the conversation',
    enricherInstanceId: 'A3M5F9K3S0',
    show: true,
    userName: '',
    streamId: 'krjijasd___12039__1jdfja23',
    userId: 123456789
}];

const delay = (duration) => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve();
        }, duration);
    });
};

const isActionValid = (data) => {
    if(!data.hasOwnProperty('showClaim')) {
        return false;
    }

    if(data.showClaim === true && (!data.hasOwnProperty('userName') || !data.hasOwnProperty('resolved'))) {
        return false;
    }

    if(data.resolve === false && !data.hasOwnProperty('isTicketRoomMember')) {
        return false;
    }

    return true;
};

describe('Action Claim Ticket Enricher', () => {
    let actionClaimTicketEnricher;
    beforeAll(() => {
        actionFactory.mockReturnValue(mockActionData);
        claimTicketActions.mockReturnValue(mockTemplate);
    })
    beforeEach(() => {
        getUserId.mockClear();
        getRooms.mockClear();
        base64.escape.mockClear();
        actionFactory.mockClear();
        subscribe.mockClear();
        claimTicketActions.mockClear();
        entityRegistry.updateEnricher.mockClear();
        renderErrorMessage.mockClear();
        getUserId.mockResolvedValue(Promise.resolve(123456789));
        getRooms.mockResolvedValue(Promise.resolve(mockInRoomList));
    });
    describe('Successful Scenarios:', () => {
        it('Should create a new action claim ticket enricher', () => {
            actionClaimTicketEnricher = new ActionClaimTicketEnricher();

            expect(MessageEnricher.mock.calls.length).toBe(1);
            expect(typeof actionClaimTicketEnricher.enrich === 'function').toBe(true);
            expect(typeof actionClaimTicketEnricher.action === 'function').toBe(true);
        });
        it('Should update enricher with agent in room', async () => {
            base64.escape.mockReturnValue(mockEntity1.streamId);

            actionClaimTicketEnricher.enrich(mockType, mockEntity1);

            await delay(10);

            expect(getUserId.mock.calls.length).toBe(1);

            expect(getRooms.mock.calls.length).toBe(1);

            expect(base64.escape.mock.calls.length).toBe(1);
            expect(base64.escape.mock.calls[0][0]).toEqual(mockInRoomList[0].threadId);

            expect(actionFactory.mock.calls.length).toBe(1);
            expect(actionFactory.mock.calls[0][0]).toEqual(expectedActions);
            expect(actionFactory.mock.calls[0][1]).toEqual('helpdesk-enricher');
            expect(actionFactory.mock.calls[0][2]).toEqual(mockEntity1);

            expect(subscribe.mock.calls.length).toBe(1);
            expect(subscribe.mock.calls[0][0]).toEqual('entity');

            expect(claimTicketActions.mock.calls.length).toBe(1);
            expect(claimTicketActions.mock.calls[0][0].isTicketRoomMember).toBe(true);
            expect(isActionValid(claimTicketActions.mock.calls[0][0])).toBe(true);

            expect(entityRegistry.updateEnricher.mock.calls.length).toBe(1);
            expect(entityRegistry.updateEnricher.mock.calls[0][0]).toEqual(mockEntity1.ticketId);
            expect(entityRegistry.updateEnricher.mock.calls[0][1]).toEqual(mockTemplate);
            expect(entityRegistry.updateEnricher.mock.calls[0][2]).toBe(mockActionData);

            expect(renderErrorMessage.mock.calls.length).toBe(0);
        });
        it('Should update enricher with agent not in room', async () => {
            base64.escape.mockReturnValue('notTheRoomWeWanted');

            actionClaimTicketEnricher.enrich(mockType, mockEntity1);

            await delay(10);

            expect(getUserId.mock.calls.length).toBe(1);

            expect(getRooms.mock.calls.length).toBe(1);

            expect(base64.escape.mock.calls.length).toBe(1);
            expect(base64.escape.mock.calls[0][0]).toEqual(mockInRoomList[0].threadId);

            expect(actionFactory.mock.calls.length).toBe(1);
            expect(actionFactory.mock.calls[0][0]).toEqual(expectedActions);
            expect(actionFactory.mock.calls[0][1]).toEqual('helpdesk-enricher');
            expect(actionFactory.mock.calls[0][2]).toEqual(mockEntity1);

            expect(subscribe.mock.calls.length).toBe(1);
            expect(subscribe.mock.calls[0][0]).toEqual('entity');

            expect(claimTicketActions.mock.calls.length).toBe(1);
            expect(claimTicketActions.mock.calls[0][0].isTicketRoomMember).toBe(false);
            expect(isActionValid(claimTicketActions.mock.calls[0][0])).toBe(true);

            expect(entityRegistry.updateEnricher.mock.calls.length).toBe(1);
            expect(entityRegistry.updateEnricher.mock.calls[0][0]).toEqual(mockEntity1.ticketId);
            expect(entityRegistry.updateEnricher.mock.calls[0][1]).toEqual(mockTemplate);
            expect(entityRegistry.updateEnricher.mock.calls[0][2]).toBe(mockActionData);

            expect(renderErrorMessage.mock.calls.length).toBe(0);
        });
        it('Should update enricher with no agent data in entity', async () => {
            actionClaimTicketEnricher.enrich(mockType, mockEntityNoAgent);

            await delay(10);

            expect(getUserId.mock.calls.length).toBe(1);

            expect(getRooms.mock.calls.length).toBe(1);

            expect(base64.escape.mock.calls.length).toBe(1);
            expect(base64.escape.mock.calls[0][0]).toEqual(mockInRoomList[0].threadId);

            expect(actionFactory.mock.calls.length).toBe(1);
            expect(actionFactory.mock.calls[0][0]).toEqual(expectedActionsNoUsername);
            expect(actionFactory.mock.calls[0][1]).toEqual('helpdesk-enricher');
            expect(actionFactory.mock.calls[0][2]).toEqual(mockEntityNoAgent);

            expect(subscribe.mock.calls.length).toBe(1);
            expect(subscribe.mock.calls[0][0]).toEqual('entity');

            expect(claimTicketActions.mock.calls.length).toBe(1);
            expect(isActionValid(claimTicketActions.mock.calls[0][0])).toBe(true);

            expect(entityRegistry.updateEnricher.mock.calls.length).toBe(1);
            expect(entityRegistry.updateEnricher.mock.calls[0][0]).toEqual(mockEntityNoAgent.ticketId);
            expect(entityRegistry.updateEnricher.mock.calls[0][1]).toEqual(mockTemplate);
            expect(entityRegistry.updateEnricher.mock.calls[0][2]).toBe(mockActionData);

            expect(renderErrorMessage.mock.calls.length).toBe(0);
        });
        it('Should not update enricher (ticket is still UNSERVICED)', async () => {
            actionClaimTicketEnricher.enrich(mockType, mockEntityUnserviced);

            await delay(10);

            expect(getUserId.mock.calls.length).toBe(0);
            expect(getRooms.mock.calls.length).toBe(0);
            expect(base64.escape.mock.calls.length).toBe(0);
            expect(actionFactory.mock.calls.length).toBe(0);
            expect(subscribe.mock.calls.length).toBe(0);
            expect(claimTicketActions.mock.calls.length).toBe(0);
            expect(entityRegistry.updateEnricher.mock.calls.length).toBe(0);
            expect(renderErrorMessage.mock.calls.length).toBe(0);
        });
        it('Should do nothing (action function has no implementation)', () => {
            actionClaimTicketEnricher.action();
        });
    });
    describe('Failing Scenarios:', () => {
        const mockErrorMessage = { messageException: 'mockMessageException' };
        const mockErrorNoMessage = 'mockError!';
        it('Should fail to get user id with no error message defined', async () => {
            getUserId.mockResolvedValue(Promise.reject(mockErrorNoMessage));

            actionClaimTicketEnricher.enrich(mockType, mockEntity1);

            await delay(10);

            expect(getUserId.mock.calls.length).toBe(1);

            expect(getRooms.mock.calls.length).toBe(0);
            expect(base64.escape.mock.calls.length).toBe(0);
            expect(actionFactory.mock.calls.length).toBe(0);
            expect(subscribe.mock.calls.length).toBe(0);
            expect(claimTicketActions.mock.calls.length).toBe(0);
            expect(entityRegistry.updateEnricher.mock.calls.length).toBe(0);

            expect(renderErrorMessage.mock.calls.length).toBe(1);
            expect(renderErrorMessage.mock.calls[0][0]).toEqual(mockEntity1);
            expect(renderErrorMessage.mock.calls[0][1]).toEqual('Could not find this user.');
            expect(renderErrorMessage.mock.calls[0][2]).toEqual('helpdesk-enricher');
        });
        it('Should fail to get user id with error message defined', async () => {
            getUserId.mockResolvedValue(Promise.reject(mockErrorMessage));

            actionClaimTicketEnricher.enrich(mockType, mockEntity1);

            await delay(10);

            expect(getUserId.mock.calls.length).toBe(1);

            expect(getRooms.mock.calls.length).toBe(0);
            expect(base64.escape.mock.calls.length).toBe(0);
            expect(actionFactory.mock.calls.length).toBe(0);
            expect(subscribe.mock.calls.length).toBe(0);
            expect(claimTicketActions.mock.calls.length).toBe(0);
            expect(entityRegistry.updateEnricher.mock.calls.length).toBe(0);

            expect(renderErrorMessage.mock.calls.length).toBe(1);
            expect(renderErrorMessage.mock.calls[0][0]).toEqual(mockEntity1);
            expect(renderErrorMessage.mock.calls[0][1]).toEqual(mockErrorMessage.messageException);
            expect(renderErrorMessage.mock.calls[0][2]).toEqual('helpdesk-enricher');
        });
        it('Should fail to get user rooms with no error message defined', async () => {
            getRooms.mockResolvedValue(Promise.reject(mockErrorNoMessage));

            actionClaimTicketEnricher.enrich(mockType, mockEntity1);

            await delay(10);

            expect(getUserId.mock.calls.length).toBe(1);
            expect(getRooms.mock.calls.length).toBe(1);

            expect(base64.escape.mock.calls.length).toBe(0);
            expect(actionFactory.mock.calls.length).toBe(0);
            expect(subscribe.mock.calls.length).toBe(0);
            expect(claimTicketActions.mock.calls.length).toBe(0);
            expect(entityRegistry.updateEnricher.mock.calls.length).toBe(0);

            expect(renderErrorMessage.mock.calls.length).toBe(1);
            expect(renderErrorMessage.mock.calls[0][0]).toEqual(mockEntity1);
            expect(renderErrorMessage.mock.calls[0][1]).toEqual('Could not get rooms for this user.');
            expect(renderErrorMessage.mock.calls[0][2]).toEqual('helpdesk-enricher');
        });
        it('Should fail to get user rooms with error message defined', async () => {
            getRooms.mockResolvedValue(Promise.reject(mockErrorMessage));

            actionClaimTicketEnricher.enrich(mockType, mockEntity1);

            await delay(10);

            expect(getUserId.mock.calls.length).toBe(1);
            expect(getRooms.mock.calls.length).toBe(1);

            expect(base64.escape.mock.calls.length).toBe(0);
            expect(actionFactory.mock.calls.length).toBe(0);
            expect(subscribe.mock.calls.length).toBe(0);
            expect(claimTicketActions.mock.calls.length).toBe(0);
            expect(entityRegistry.updateEnricher.mock.calls.length).toBe(0);

            expect(renderErrorMessage.mock.calls.length).toBe(1);
            expect(renderErrorMessage.mock.calls[0][0]).toEqual(mockEntity1);
            expect(renderErrorMessage.mock.calls[0][1]).toEqual(mockErrorMessage.messageException);
            expect(renderErrorMessage.mock.calls[0][2]).toEqual('helpdesk-enricher');
        });
    });
});