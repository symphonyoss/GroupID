import { MessageEnricherBase } from 'symphony-integration-commons';
import actionFactory from '../../utils/actionFactory';
import AttachmentService from '../../services/attachmentService';
import { getUserId } from '../../utils/userUtils';
import { renderErrorMessage } from '../../utils/errorMessage';
import attachmentActions from '../../templates/attachmentActions.hbs';

import AttachmentEnricher from '../attachmentEnricher';
import {approveAttachment} from "../../api/apiCalls";

jest.mock('symphony-integration-commons');
jest.mock('../../utils/actionFactory');
jest.mock('../../services/attachmentService');
jest.mock('../../utils/userUtils');
jest.mock('../../utils/errorMessage');
jest.mock('../../templates/attachmentActions.hbs');

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

const mockAttachmentService = {
    search: jest.fn(),
    approve: jest.fn(),
    deny: jest.fn()
};

const mockerId = 123456789;

const mockerChekcerId = 987654321;

const mockType = 'mockType';

const mockDataApprove = {
    type: 'approveAttachment',
    enricherInstanceId: 'mockEnricherInstanceId',
    entity: {}
};

const mockDataDeny = {
    type: 'denyAttachment',
    enricherInstanceId: 'mockEnricherInstanceId',
    entity: {}
};

const mockEntity1 = {
    attachmentUrl: 'attachment.url/13232',
    makerId: mockerId,
    makerCheckerId: 'mockerCheckerId',
    streamId: 'krjijasd___12039__1jdfja23'
};

const mockEntityNoUrl = {
    makerId: mockerId,
    makerCheckerId: 'mockerCheckerId',
    streamId: 'krjijasd___12039__1jdfja23'
};

const mockAttachmentOpen = {
    state: 'OPEN',
    checker: {
        displayName: 'mockChecker'
    }
};

const mockAttachmentNoDisplayName = {
    state: 'OPEN',
};

const mockAttachmentApproved = {
    state: 'APPROVED',
    checker: {
        displayName: 'mockChecker'
    }
};

const delay = (duration) => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve();
        }, duration);
    });
};

const isActionValid = (data) => {
    if(!data.hasOwnProperty('showActions')) {
        return false;
    }

    if(data.showActions === true && !data.hasOwnProperty('showButtons')) {
        return false;
    }

    if(data.showActions === false && (!data.hasOwnProperty('isApproved') || !data.hasOwnProperty('userName'))) {
        return false;
    }

    return true;
};

const mockErrorMessage = { messageException: 'mockMessageException' };
const mockErrorNoMessage = 'mockError!';

describe('Attachment Enricher', () => {
    let attachmentEnricher;
    beforeAll(() => {
        AttachmentService.mockClear();
    });
    it('Should create a new attachment enricher', () => {
        AttachmentService.mockImplementation(() => {
            return mockAttachmentService;
        });

        attachmentEnricher = new AttachmentEnricher();

        expect(MessageEnricherBase.mock.calls.length).toBe(1);
        expect(AttachmentService.mock.calls.length).toBe(1);
        expect(attachmentEnricher.services.attachmentService).toEqual(mockAttachmentService);

        expect(typeof attachmentEnricher.enrich === 'function').toBe(true);
        expect(typeof attachmentEnricher.action === 'function').toBe(true);
    });
    describe('Enrich:', () => {
        beforeEach(() => {
            getUserId.mockClear();
            actionFactory.mockClear();
            attachmentActions.mockClear();
            mockAttachmentService.search.mockClear();
            renderErrorMessage.mockClear();
        });
        it('Should enrich with deny and approve actions (checker case)', async () => {
            mockAttachmentService.search.mockResolvedValue({ data: mockAttachmentOpen });
            getUserId.mockResolvedValue(mockerChekcerId);

            attachmentEnricher.enrich(mockType, mockEntity1);

            await delay(10);

            expect(mockAttachmentService.search.mock.calls.length).toBe(1);

            expect(getUserId.mock.calls.length).toBe(1);

            expect(actionFactory.mock.calls.length).toBe(1);
            expect(Array.isArray(actionFactory.mock.calls[0][0])).toBe(true);
            expect(actionFactory.mock.calls[0][0].length).toBe(2);
            expect(typeof actionFactory.mock.calls[0][1]).toEqual('string');
            expect(actionFactory.mock.calls[0][2]).toEqual(mockEntity1);

            expect(attachmentActions.mock.calls.length).toBe(1);
            expect(attachmentActions.mock.calls[0][0].showActions).toBe(true);
            expect(attachmentActions.mock.calls[0][0].showButtons).toBe(true);
            expect(isActionValid(attachmentActions.mock.calls[0][0])).toBe(true);

            expect(renderErrorMessage.mock.calls.length).toBe(0);
        });
        it('Should enrich with deny and approve actions (no displayName case)', async () => {
            mockAttachmentService.search.mockResolvedValue({ data: mockAttachmentNoDisplayName });
            getUserId.mockResolvedValue(mockerChekcerId);

            attachmentEnricher.enrich(mockType, mockEntity1);

            await delay(10);

            expect(mockAttachmentService.search.mock.calls.length).toBe(1);

            expect(getUserId.mock.calls.length).toBe(1);

            expect(actionFactory.mock.calls.length).toBe(1);
            expect(Array.isArray(actionFactory.mock.calls[0][0])).toBe(true);
            expect(actionFactory.mock.calls[0][0].length).toBe(2);
            expect(typeof actionFactory.mock.calls[0][1]).toEqual('string');
            expect(actionFactory.mock.calls[0][2]).toEqual(mockEntity1);

            expect(attachmentActions.mock.calls.length).toBe(1);
            expect(attachmentActions.mock.calls[0][0].showActions).toBe(true);
            expect(attachmentActions.mock.calls[0][0].showButtons).toBe(true);
            expect(isActionValid(attachmentActions.mock.calls[0][0])).toBe(true);

            expect(renderErrorMessage.mock.calls.length).toBe(0);
        });
        it('Should enrich with no actions (maker case)', async () => {
            mockAttachmentService.search.mockResolvedValue({ data: mockAttachmentOpen });
            getUserId.mockResolvedValue(mockerId);

            attachmentEnricher.enrich(mockType, mockEntity1);

            await delay(10);

            expect(mockAttachmentService.search.mock.calls.length).toBe(1);

            expect(getUserId.mock.calls.length).toBe(1);

            expect(actionFactory.mock.calls.length).toBe(1);
            expect(Array.isArray(actionFactory.mock.calls[0][0])).toBe(true);
            expect(actionFactory.mock.calls[0][0].length).toBe(2);
            expect(typeof actionFactory.mock.calls[0][1]).toEqual('string');
            expect(actionFactory.mock.calls[0][2]).toEqual(mockEntity1);

            expect(attachmentActions.mock.calls.length).toBe(1);
            expect(attachmentActions.mock.calls[0][0].showActions).toBe(true);
            expect(attachmentActions.mock.calls[0][0].showButtons).toBe(false);
            expect(isActionValid(attachmentActions.mock.calls[0][0])).toBe(true);

            expect(renderErrorMessage.mock.calls.length).toBe(0);
        });
        it('Should enrich with no actions (attachment already APPROVED)', async () => {
            mockAttachmentService.search.mockResolvedValue({ data: mockAttachmentApproved });
            getUserId.mockResolvedValue(mockerChekcerId);

            attachmentEnricher.enrich(mockType, mockEntity1);

            await delay(10);

            expect(mockAttachmentService.search.mock.calls.length).toBe(1);

            expect(getUserId.mock.calls.length).toBe(1);

            expect(actionFactory.mock.calls.length).toBe(1);
            expect(Array.isArray(actionFactory.mock.calls[0][0])).toBe(true);
            expect(actionFactory.mock.calls[0][0].length).toBe(2);
            expect(typeof actionFactory.mock.calls[0][1]).toEqual('string');
            expect(actionFactory.mock.calls[0][2]).toEqual(mockEntity1);

            expect(attachmentActions.mock.calls.length).toBe(1);
            expect(attachmentActions.mock.calls[0][0].showActions).toBe(false);
            expect(attachmentActions.mock.calls[0][0].showButtons).toBe(true);
            expect(isActionValid(attachmentActions.mock.calls[0][0])).toBe(true);

            expect(renderErrorMessage.mock.calls.length).toBe(0);
        });
        it('Should render error (no attachment url)', async () => {
            attachmentEnricher.enrich(mockType, mockEntityNoUrl);

            await delay(10);

            expect(mockAttachmentService.search.mock.calls.length).toBe(0);
            expect(getUserId.mock.calls.length).toBe(0);
            expect(actionFactory.mock.calls.length).toBe(0);
            expect(attachmentActions.mock.calls.length).toBe(0);

            expect(renderErrorMessage.mock.calls.length).toBe(1);
            expect(renderErrorMessage.mock.calls[0][0]).toEqual(mockEntityNoUrl);
            expect(typeof renderErrorMessage.mock.calls[0][1]).toEqual('string');
            expect(typeof renderErrorMessage.mock.calls[0][2]).toEqual('string');
        });
        it('Should render error (status 204 by API)', async () => {
            mockAttachmentService.search.mockResolvedValue({ status: 204 });

            attachmentEnricher.enrich(mockType, mockEntity1);

            await delay(10);

            expect(mockAttachmentService.search.mock.calls.length).toBe(1);

            expect(getUserId.mock.calls.length).toBe(0);
            expect(actionFactory.mock.calls.length).toBe(0);
            expect(attachmentActions.mock.calls.length).toBe(0);

            expect(renderErrorMessage.mock.calls.length).toBe(1);
            expect(renderErrorMessage.mock.calls[0][0]).toEqual(mockEntity1);
            expect(typeof renderErrorMessage.mock.calls[0][1]).toEqual('string');
            expect(typeof renderErrorMessage.mock.calls[0][2]).toEqual('string');
        });
        it('Should render error (API exception with message)', async () => {
            mockAttachmentService.search.mockResolvedValue(Promise.reject(mockErrorMessage));

            attachmentEnricher.enrich(mockType, mockEntity1);

            await delay(10);

            expect(mockAttachmentService.search.mock.calls.length).toBe(1);

            expect(getUserId.mock.calls.length).toBe(0);
            expect(actionFactory.mock.calls.length).toBe(0);
            expect(attachmentActions.mock.calls.length).toBe(0);

            expect(renderErrorMessage.mock.calls.length).toBe(1);
            expect(renderErrorMessage.mock.calls[0][0]).toEqual(mockEntity1);
            expect(renderErrorMessage.mock.calls[0][1]).toEqual(mockErrorMessage.messageException);
            expect(typeof renderErrorMessage.mock.calls[0][2]).toEqual('string');
        });
        it('Should render error (API exception with no message)', async () => {
            mockAttachmentService.search.mockResolvedValue(Promise.reject(mockErrorNoMessage));

            attachmentEnricher.enrich(mockType, mockEntity1);

            await delay(10);

            expect(mockAttachmentService.search.mock.calls.length).toBe(1);

            expect(getUserId.mock.calls.length).toBe(0);
            expect(actionFactory.mock.calls.length).toBe(0);
            expect(attachmentActions.mock.calls.length).toBe(0);

            expect(renderErrorMessage.mock.calls.length).toBe(1);
            expect(renderErrorMessage.mock.calls[0][0]).toEqual(mockEntity1);
            expect(typeof renderErrorMessage.mock.calls[0][1]).toEqual('string');
            expect(typeof renderErrorMessage.mock.calls[0][2]).toEqual('string');
        });
    });
    describe('Action:', () => {
        beforeEach(() => {
            subscribe.mockClear();
            entityRegistry.updateEnricher.mockClear();
            getUserId.mockClear();
            actionFactory.mockClear();
            attachmentActions.mockClear();
            mockAttachmentService.search.mockClear();
            renderErrorMessage.mockClear();
            mockAttachmentService.approve.mockClear();
            mockAttachmentService.deny.mockClear();
        });
        it('Should update enricher on approved attachment', async () => {
            mockAttachmentService.approve.mockResolvedValue({ data: { user: { displayName: 'lulba' } } });

            attachmentEnricher.action(mockDataApprove);

            await delay(10);

            expect(subscribe.mock.calls.length).toBe(1);

            expect(actionFactory.mock.calls.length).toBe(1);
            expect(Array.isArray(actionFactory.mock.calls[0][0])).toBe(true);
            expect(actionFactory.mock.calls[0][0].length).toBe(0);
            expect(typeof actionFactory.mock.calls[0][1]).toEqual('string');
            expect(actionFactory.mock.calls[0][2]).toEqual(mockDataApprove.entity);

            expect(mockAttachmentService.approve.mock.calls.length).toBe(1);
            expect(mockAttachmentService.approve.mock.calls[0][0]).toEqual(mockDataApprove);

            expect(attachmentActions.mock.calls.length).toBe(1);
            expect(attachmentActions.mock.calls[0][0].showActions).toBe(false);
            expect(attachmentActions.mock.calls[0][0].isApproved).toBe(true);
            expect(isActionValid(attachmentActions.mock.calls[0][0])).toBe(true);

            expect(entityRegistry.updateEnricher.mock.calls.length).toBe(1);
            expect(entityRegistry.updateEnricher.mock.calls[0][0]).toEqual(mockDataApprove.enricherInstanceId);
            expect(entityRegistry.updateEnricher.mock.calls[0][1]).toBe(undefined);
            expect(entityRegistry.updateEnricher.mock.calls[0][2]).toBe(undefined);
        });
        it('Should update enricher on denied attachment', async () => {
            mockAttachmentService.deny.mockResolvedValue({ data: { user: { displayName: 'lulba' } } });

            attachmentEnricher.action(mockDataDeny);

            await delay(10);

            expect(subscribe.mock.calls.length).toBe(1);

            expect(actionFactory.mock.calls.length).toBe(1);
            expect(Array.isArray(actionFactory.mock.calls[0][0])).toBe(true);
            expect(actionFactory.mock.calls[0][0].length).toBe(0);
            expect(typeof actionFactory.mock.calls[0][1]).toEqual('string');
            expect(actionFactory.mock.calls[0][2]).toEqual(mockDataDeny.entity);

            expect(mockAttachmentService.deny.mock.calls.length).toBe(1);
            expect(mockAttachmentService.deny.mock.calls[0][0]).toEqual(mockDataDeny);

            expect(attachmentActions.mock.calls.length).toBe(1);
            expect(attachmentActions.mock.calls[0][0].showActions).toBe(false);
            expect(attachmentActions.mock.calls[0][0].isApproved).toBe(false);
            expect(isActionValid(attachmentActions.mock.calls[0][0])).toBe(true);

            expect(entityRegistry.updateEnricher.mock.calls.length).toBe(1);
            expect(entityRegistry.updateEnricher.mock.calls[0][0]).toEqual(mockDataDeny.enricherInstanceId);
            expect(entityRegistry.updateEnricher.mock.calls[0][1]).toBe(undefined);
            expect(entityRegistry.updateEnricher.mock.calls[0][2]).toBe(undefined);
        });
        it('Should update enricher on approved attachment (no user displayName case)', async () => {
            mockAttachmentService.approve.mockResolvedValue({ data: { user: {} } });

            attachmentEnricher.action(mockDataApprove);

            await delay(10);

            expect(subscribe.mock.calls.length).toBe(1);

            expect(actionFactory.mock.calls.length).toBe(1);
            expect(Array.isArray(actionFactory.mock.calls[0][0])).toBe(true);
            expect(actionFactory.mock.calls[0][0].length).toBe(0);
            expect(typeof actionFactory.mock.calls[0][1]).toEqual('string');
            expect(actionFactory.mock.calls[0][2]).toEqual(mockDataApprove.entity);

            expect(mockAttachmentService.approve.mock.calls.length).toBe(1);
            expect(mockAttachmentService.approve.mock.calls[0][0]).toEqual(mockDataApprove);

            expect(attachmentActions.mock.calls.length).toBe(1);
            expect(attachmentActions.mock.calls[0][0].showActions).toBe(false);
            expect(attachmentActions.mock.calls[0][0].isApproved).toBe(true);
            expect(isActionValid(attachmentActions.mock.calls[0][0])).toBe(true);

            expect(entityRegistry.updateEnricher.mock.calls.length).toBe(1);
            expect(entityRegistry.updateEnricher.mock.calls[0][0]).toEqual(mockDataApprove.enricherInstanceId);
            expect(entityRegistry.updateEnricher.mock.calls[0][1]).toBe(undefined);
            expect(entityRegistry.updateEnricher.mock.calls[0][2]).toBe(undefined);
        });
        it('Should update enricher on denied attachment (no user displayName case)', async () => {
            mockAttachmentService.deny.mockResolvedValue({ data: { user: {} } });

            attachmentEnricher.action(mockDataDeny);

            await delay(10);

            expect(subscribe.mock.calls.length).toBe(1);

            expect(actionFactory.mock.calls.length).toBe(1);
            expect(Array.isArray(actionFactory.mock.calls[0][0])).toBe(true);
            expect(actionFactory.mock.calls[0][0].length).toBe(0);
            expect(typeof actionFactory.mock.calls[0][1]).toEqual('string');
            expect(actionFactory.mock.calls[0][2]).toEqual(mockDataDeny.entity);

            expect(mockAttachmentService.deny.mock.calls.length).toBe(1);
            expect(mockAttachmentService.deny.mock.calls[0][0]).toEqual(mockDataDeny);

            expect(attachmentActions.mock.calls.length).toBe(1);
            expect(attachmentActions.mock.calls[0][0].showActions).toBe(false);
            expect(attachmentActions.mock.calls[0][0].isApproved).toBe(false);
            expect(isActionValid(attachmentActions.mock.calls[0][0])).toBe(true);

            expect(entityRegistry.updateEnricher.mock.calls.length).toBe(1);
            expect(entityRegistry.updateEnricher.mock.calls[0][0]).toEqual(mockDataDeny.enricherInstanceId);
            expect(entityRegistry.updateEnricher.mock.calls[0][1]).toBe(undefined);
            expect(entityRegistry.updateEnricher.mock.calls[0][2]).toBe(undefined);
        });
    });
});