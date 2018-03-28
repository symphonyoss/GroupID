import { MessageEnricherBase } from 'symphony-integration-commons';
import actionFactory from '../../utils/actionFactory';
import TicketService from '../../services/ticketService';
import { getUserId, getRooms } from '../../utils/userUtils';
import { renderErrorMessage } from '../../utils/errorMessage';
import claimTicketActions from '../../templates/claimTicketActions.hbs';
const base64 = require('base64-url');

import ClaimTicketEnricher from '../claimTicketEnricher';

jest.mock('symphony-integration-commons');
jest.mock('../../utils/actionFactory');
jest.mock('../../services/ticketService');
jest.mock('../../utils/userUtils');
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

const mockTicketService = {
    getTicket: jest.fn(),
    claim: jest.fn(),
    join: jest.fn()
};

const mockUserId = 12345689;

const mockNotInRoom = [{ threadId: 'notInThisRoom!' }];
const mockInRoom = [{ threadId: 'krjijasd___12039__1jdfja23' }];

const mockType = 'mockType';

const mockEntity = {
    ticketUrl: 'ticket.url/asdf2312sdf/',
    ticketId: 'asdf2312sdf',
    streamId: 'krjijasd___12039__1jdfja23'
};

const mockEntityNoTicketUrl = {
    ticketId: 'asdf2312sdf',
    streamId: 'krjijasd___12039__1jdfja23'
};

const mockTicketNotFound = {
    code: '204'
};

const mockUnservicedTicket = {
    data: {
        state: 'UNSERVICED',
        serviceStreamId: 'krjijasd___12039__1jdfja23'
    }
};

const mockUnresolvedTicket = {
    data: {
        agent: {
            displayName: 'mockDisplayName'
        },
        state: 'UNRESOLVED',
        serviceStreamId: 'krjijasd___12039__1jdfja23'
    }
};

const mockResolvedTicket = {
    data: {
        state: 'RESOLVED',
        serviceStreamId: 'krjijasd___12039__1jdfja23'
    }
};

const mockData = {
    claimTicket: {
        data: {}
    },
    joinConversation: {
        data: {}
    }
};

const isActionValid = (data) => {
    if(!data.hasOwnProperty('showClaim')) {
        return false;
    }

    if(data.showClaim === false && (!data.hasOwnProperty('userName') || (!data.hasOwnProperty('resolved')))) {
        return false;
    }

    if(data.resolved === false && !data.hasOwnProperty('isTicketRoomMember')) {
        return false;
    }

    return true;
};

const mockActionDataClaim = {
    type: 'claimTicket',
    data: {
        user: {}
    },
    entity: 'mockEntity'
};

const mockActionDataJoin = {
    type: 'joinConversation',
    data: {
        user: {}
    },
    entity: 'mockEntity'
};

const mockActionDataDefault = {
    type: 'default',
    data: {
        user: {}
    },
    entity: 'mockEntity'
};

const delay = (duration) => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve();
        }, duration);
    });
};

const mockTemplate = 'mockTemplate';

const expectedActionsClaim = [ { id: 'claimTicket',
    service: 'helpdesk-enricher',
    type: 'claimTicket',
    label: 'Claim',
    enricherInstanceId: 'asdf2312sdf',
    show: true,
    userName: '',
    streamId: 'krjijasd___12039__1jdfja23',
    userId: 12345689 },
    { id: 'joinConversation',
        service: 'helpdesk-enricher',
        type: 'joinConversation',
        label: 'Join the conversation',
        enricherInstanceId: 'asdf2312sdf',
        show: false,
        userName: '',
        streamId: 'krjijasd___12039__1jdfja23',
        userId: 12345689
    } ];

const expectedActionsJoin = [ { id: 'claimTicket',
    service: 'helpdesk-enricher',
    type: 'claimTicket',
    label: 'Claim',
    enricherInstanceId: 'asdf2312sdf',
    show: false,
    userName: 'mockDisplayName',
    streamId: 'krjijasd___12039__1jdfja23',
    userId: 12345689 },
    { id: 'joinConversation',
        service: 'helpdesk-enricher',
        type: 'joinConversation',
        label: 'Join the conversation',
        enricherInstanceId: 'asdf2312sdf',
        show: true,
        userName: 'mockDisplayName',
        streamId: 'krjijasd___12039__1jdfja23',
        userId: 12345689
    } ];

const expectedActionOnClaim = [{
    enricherInstanceId: undefined,
    id: 'claimTicket',
    label: 'Claim',
    service: 'helpdesk-enricher',
    show: false,
    type: 'claimTicket',
    userName: undefined
}];

const expectedActionOnJoin = [{
    enricherInstanceId: undefined,
    id: 'joinConversation',
    label: 'Join the conversation',
    service: 'helpdesk-enricher',
    show: false,
    type: 'joinConversation',
    userName: undefined
}];

describe('Claim Ticket Enricher', () => {
    let claimTicketEnricher;
    beforeAll(() => {
        claimTicketActions.mockReturnValue(mockTemplate);
    });
    beforeEach(() => {
        MessageEnricherBase.mockClear();
        getUserId.mockClear();
        getRooms.mockClear();
        base64.escape.mockClear();
        actionFactory.mockClear();
        claimTicketActions.mockClear();
        renderErrorMessage.mockClear();
        subscribe.mockClear();
        entityRegistry.updateEnricher.mockClear();
    });
    it('Should create a new claim ticket enricher', () => {
        TicketService.mockImplementation(() => {
            return mockTicketService;
        });

        claimTicketEnricher = new ClaimTicketEnricher();

        expect(MessageEnricherBase.mock.calls.length).toBe(1);
        expect(TicketService.mock.calls.length).toBe(1);
        expect(claimTicketEnricher.services.ticketService).toEqual(mockTicketService);

        expect(typeof claimTicketEnricher.enrich === 'function').toBe(true);
        expect(typeof claimTicketEnricher.action === 'function').toBe(true);
    });
    describe('Enrich', () => {
        beforeEach(() => {
            mockTicketService.getTicket.mockClear();
        });
        describe('Successful Scenarios:', () => {
            it('Should enrich with claim button', async () => {
                mockTicketService.getTicket.mockResolvedValue(mockUnservicedTicket);
                getUserId.mockResolvedValue(mockUserId);
                getRooms.mockResolvedValue(mockNotInRoom);
                base64.escape.mockReturnValue(mockNotInRoom[0].threadId);
                actionFactory.mockReturnValue(mockData);

                claimTicketEnricher.enrich(mockType, mockEntity);

                await delay(10);

                expect(mockTicketService.getTicket.mock.calls.length).toBe(1);
                expect(mockTicketService.getTicket.mock.calls[0][0]).toEqual(mockEntity.ticketUrl);

                expect(getUserId.mock.calls.length).toBe(1);

                expect(getRooms.mock.calls.length).toBe(1);

                expect(base64.escape.mock.calls.length).toBe(mockNotInRoom.length);
                for(const room of mockNotInRoom) {
                    expect(base64.escape.mock.calls).toContainEqual([room.threadId]);
                }

                expect(actionFactory.mock.calls.length).toBe(1);
                expect(actionFactory.mock.calls[0][0]).toEqual(expectedActionsClaim);
                let containsClaim = false;
                let containsJoin = false;
                for(const actionObj of actionFactory.mock.calls[0][0]) {
                    if(actionObj.id === 'claimTicket') {
                        containsClaim = true;
                        expect(actionObj.show).toBe(true);
                    }
                    if(actionObj.id === 'joinConversation') {
                        containsJoin = true;
                        expect(actionObj.show).toBe(false);
                    }
                }
                expect(containsClaim).toBe(true);
                expect(containsJoin).toBe(true);
                expect(actionFactory.mock.calls[0][1]).toEqual('helpdesk-enricher');
                expect(actionFactory.mock.calls[0][2]).toEqual(mockEntity);

                expect(claimTicketActions.mock.calls.length).toBe(1);
                expect(isActionValid(claimTicketActions.mock.calls[0][0])).toBe(true);
                expect(claimTicketActions.mock.calls[0][0].resolved).toBe(false);
                expect(claimTicketActions.mock.calls[0][0].isTicketRoomMember).toBe(false);

                expect(renderErrorMessage.mock.calls.length).toBe(0);
            });
            it('Should enrich with join button', async () => {
                mockTicketService.getTicket.mockResolvedValue(mockUnresolvedTicket);
                getUserId.mockResolvedValue(mockUserId);
                getRooms.mockResolvedValue(mockNotInRoom);
                base64.escape.mockReturnValue(mockNotInRoom[0].threadId);
                actionFactory.mockReturnValue(mockData);

                claimTicketEnricher.enrich(mockType, mockEntity);

                await delay(10);

                expect(mockTicketService.getTicket.mock.calls.length).toBe(1);
                expect(mockTicketService.getTicket.mock.calls[0][0]).toEqual(mockEntity.ticketUrl);

                expect(getUserId.mock.calls.length).toBe(1);

                expect(getRooms.mock.calls.length).toBe(1);

                expect(base64.escape.mock.calls.length).toBe(mockNotInRoom.length);
                for(const room of mockNotInRoom) {
                    expect(base64.escape.mock.calls).toContainEqual([room.threadId]);
                }

                expect(actionFactory.mock.calls.length).toBe(1);
                expect(actionFactory.mock.calls[0][0]).toEqual(expectedActionsJoin);
                let containsClaim = false;
                let containsJoin = false;
                for(const actionObj of actionFactory.mock.calls[0][0]) {
                    if(actionObj.id === 'claimTicket') {
                        containsClaim = true;
                        expect(actionObj.show).toBe(false);
                    }
                    if(actionObj.id === 'joinConversation') {
                        containsJoin = true;
                        expect(actionObj.show).toBe(true);
                    }
                }
                expect(containsClaim).toBe(true);
                expect(containsJoin).toBe(true);
                expect(actionFactory.mock.calls[0][1]).toEqual('helpdesk-enricher');
                expect(actionFactory.mock.calls[0][2]).toEqual(mockEntity);

                expect(claimTicketActions.mock.calls.length).toBe(1);
                expect(isActionValid(claimTicketActions.mock.calls[0][0])).toBe(true);
                expect(claimTicketActions.mock.calls[0][0].resolved).toBe(false);
                expect(claimTicketActions.mock.calls[0][0].isTicketRoomMember).toBe(false);

                expect(renderErrorMessage.mock.calls.length).toBe(0);
            });
            it('Should enrich agent in room message', async () => {
                mockTicketService.getTicket.mockResolvedValue(mockUnresolvedTicket);
                getUserId.mockResolvedValue(mockUserId);
                getRooms.mockResolvedValue(mockInRoom);
                base64.escape.mockReturnValue(mockInRoom[0].threadId);
                actionFactory.mockReturnValue(mockData);

                claimTicketEnricher.enrich(mockType, mockEntity);

                await delay(10);

                expect(mockTicketService.getTicket.mock.calls.length).toBe(1);
                expect(mockTicketService.getTicket.mock.calls[0][0]).toEqual(mockEntity.ticketUrl);

                expect(getUserId.mock.calls.length).toBe(1);

                expect(getRooms.mock.calls.length).toBe(1);

                expect(base64.escape.mock.calls.length).toBe(mockInRoom.length);
                for(const room of mockInRoom) {
                    expect(base64.escape.mock.calls).toContainEqual([room.threadId]);
                }

                expect(claimTicketActions.mock.calls.length).toBe(1);
                expect(isActionValid(claimTicketActions.mock.calls[0][0])).toBe(true);
                expect(claimTicketActions.mock.calls[0][0].resolved).toBe(false);
                expect(claimTicketActions.mock.calls[0][0].isTicketRoomMember).toBe(true);

                expect(renderErrorMessage.mock.calls.length).toBe(0);
            });
            it('Should enrich ticket closed message', async () => {
                mockTicketService.getTicket.mockResolvedValue(mockResolvedTicket);

                claimTicketEnricher.enrich(mockType, mockEntity);

                await delay(10);

                expect(mockTicketService.getTicket.mock.calls.length).toBe(1);
                expect(mockTicketService.getTicket.mock.calls[0][0]).toEqual(mockEntity.ticketUrl);

                expect(claimTicketActions.mock.calls.length).toBe(1);
                expect(isActionValid(claimTicketActions.mock.calls[0][0])).toBe(true);
                expect(claimTicketActions.mock.calls[0][0].resolved).toBe(true);

                expect(renderErrorMessage.mock.calls.length).toBe(0);
            });
        });
        describe('Failing Scenarios:', () => {
            it('Should render error (no ticket url)', () => {
                claimTicketEnricher.enrich(mockType, mockEntityNoTicketUrl);

                expect(renderErrorMessage.mock.calls.length).toBe(1);
                expect(renderErrorMessage.mock.calls[0][0]).toEqual(mockEntityNoTicketUrl);
                expect(renderErrorMessage.mock.calls[0][1]).toEqual('Cannot retrieve ticket state.');
                expect(renderErrorMessage.mock.calls[0][2]).toEqual('helpdesk-enricher');
            });
            it('Should render error (API status 204)', async () => {
                mockTicketService.getTicket.mockResolvedValue(mockTicketNotFound);
                getUserId.mockResolvedValue(mockUserId);
                getRooms.mockResolvedValue(mockInRoom);

                claimTicketEnricher.enrich(mockType, mockEntity);

                await delay(10);

                expect(mockTicketService.getTicket.mock.calls.length).toBe(1);
                expect(mockTicketService.getTicket.mock.calls[0][0]).toEqual(mockEntity.ticketUrl);

                expect(renderErrorMessage.mock.calls.length).toBe(1);
                expect(renderErrorMessage.mock.calls[0][0]).toEqual(mockEntity);
                expect(renderErrorMessage.mock.calls[0][1]).toEqual('Ticket not found.');
                expect(renderErrorMessage.mock.calls[0][2]).toEqual('helpdesk-enricher');
            });
            it('Should render error (API failure in getTicket)', async () => {
                mockTicketService.getTicket.mockResolvedValue(Promise.reject({}));

                claimTicketEnricher.enrich(mockType, mockEntity);

                await delay(10);

                expect(mockTicketService.getTicket.mock.calls.length).toBe(1);
                expect(mockTicketService.getTicket.mock.calls[0][0]).toEqual(mockEntity.ticketUrl);

                expect(renderErrorMessage.mock.calls.length).toBe(1);
                expect(renderErrorMessage.mock.calls[0][1]).toEqual('Cannot retrieve ticket state.');
                expect(renderErrorMessage.mock.calls[0][2]).toEqual('helpdesk-enricher');
            });
            it('Should render error (extensions-api failure in getUserId)', async () => {
                mockTicketService.getTicket.mockResolvedValue(mockEntity);
                getUserId.mockResolvedValue(Promise.reject({}));
                getRooms.mockResolvedValue(mockInRoom);

                claimTicketEnricher.enrich(mockType, mockEntity);

                await delay(10);

                expect(mockTicketService.getTicket.mock.calls.length).toBe(1);
                expect(mockTicketService.getTicket.mock.calls[0][0]).toEqual(mockEntity.ticketUrl);

                expect(renderErrorMessage.mock.calls.length).toBe(1);
                expect(renderErrorMessage.mock.calls[0][1]).toEqual('Cannot retrieve ticket state.');
                expect(renderErrorMessage.mock.calls[0][2]).toEqual('helpdesk-enricher');
            });
            it('Should render error (extensions-api failure in getRooms)', async () => {
                mockTicketService.getTicket.mockResolvedValue(mockEntity);
                getUserId.mockResolvedValue(mockUserId);
                getRooms.mockResolvedValue(Promise.reject({}));

                claimTicketEnricher.enrich(mockType, mockEntity);

                await delay(10);

                expect(mockTicketService.getTicket.mock.calls.length).toBe(1);
                expect(mockTicketService.getTicket.mock.calls[0][0]).toEqual(mockEntity.ticketUrl);

                expect(renderErrorMessage.mock.calls.length).toBe(1);
                expect(renderErrorMessage.mock.calls[0][1]).toEqual('Cannot retrieve ticket state.');
                expect(renderErrorMessage.mock.calls[0][2]).toEqual('helpdesk-enricher');
            });
        });
    });
    describe('Action', () => {
        beforeEach(() => {
            mockTicketService.claim.mockClear();
            mockTicketService.join.mockClear();
        });
        it('Should update enricher on claim ticket', async () => {
            mockTicketService.claim.mockResolvedValue(mockActionDataClaim);
            actionFactory.mockReturnValue(mockData);

            claimTicketEnricher.action(mockActionDataClaim);

            await delay(10);

            expect(mockTicketService.claim.mock.calls.length).toBe(1);
            expect(mockTicketService.claim.mock.calls[0][0]).toEqual(mockActionDataClaim);

            expect(subscribe.mock.calls.length).toBe(1);
            expect(subscribe.mock.calls[0][0]).toEqual('entity');

            expect(actionFactory.mock.calls.length).toBe(1);
            expect(actionFactory.mock.calls[0][0]).toEqual(expectedActionOnClaim);
            expect(actionFactory.mock.calls[0][1]).toEqual('helpdesk-enricher');
            expect(actionFactory.mock.calls[0][2]).toEqual(mockActionDataClaim.entity);

            expect(claimTicketActions.mock.calls.length).toBe(1);
            expect(isActionValid(claimTicketActions.mock.calls[0][0])).toBe(true);
            expect(claimTicketActions.mock.calls[0][0].resolved).toBe(false);
            expect(claimTicketActions.mock.calls[0][0].isTicketRoomMember).toBe(true);

            expect(entityRegistry.updateEnricher.mock.calls.length).toBe(1);
            expect(entityRegistry.updateEnricher.mock.calls[0][0]).toEqual(mockActionDataClaim.enricherInstanceId);
            expect(entityRegistry.updateEnricher.mock.calls[0][1]).toBe(mockTemplate);
            expect(entityRegistry.updateEnricher.mock.calls[0][2]).toBe(mockData);
        });
        it('Should update enricher on join conversation', async () => {
            mockTicketService.join.mockResolvedValue(mockActionDataJoin);
            actionFactory.mockReturnValue(mockData);

            claimTicketEnricher.action(mockActionDataJoin);

            await delay(10);

            expect(mockTicketService.join.mock.calls.length).toBe(1);
            expect(mockTicketService.join.mock.calls[0][0]).toEqual(mockActionDataJoin);

            expect(subscribe.mock.calls.length).toBe(1);
            expect(subscribe.mock.calls[0][0]).toEqual('entity');

            expect(actionFactory.mock.calls.length).toBe(1);
            expect(actionFactory.mock.calls[0][0]).toEqual(expectedActionOnJoin);
            expect(actionFactory.mock.calls[0][1]).toEqual('helpdesk-enricher');
            expect(actionFactory.mock.calls[0][2]).toEqual(mockActionDataJoin.entity);

            expect(claimTicketActions.mock.calls.length).toBe(1);
            expect(isActionValid(claimTicketActions.mock.calls[0][0])).toBe(true);
            expect(claimTicketActions.mock.calls[0][0].resolved).toBe(false);
            expect(claimTicketActions.mock.calls[0][0].isTicketRoomMember).toBe(true);

            expect(entityRegistry.updateEnricher.mock.calls.length).toBe(1);
            expect(entityRegistry.updateEnricher.mock.calls[0][0]).toEqual(mockActionDataClaim.enricherInstanceId);
            expect(entityRegistry.updateEnricher.mock.calls[0][1]).toBe(mockTemplate);
            expect(entityRegistry.updateEnricher.mock.calls[0][2]).toBe(mockData);
        });
        it('Should do nothing (default action)', async() => {
            claimTicketEnricher.action(mockActionDataDefault);
        });
    });
});