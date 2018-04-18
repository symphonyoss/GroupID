import MessageEnricher from '../messageEnricher';
import actionFactory from '../../utils/actionFactory';
import attachmentActions from '../../templates/attachmentActions.hbs';

import ActionAttachmentEnricher from '../actionAttachmentEnricher';

jest.mock('../messageEnricher');
jest.mock('../../utils/actionFactory');
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

const mockType = 'mockType';

const mockEntityApproved = {
    state: 'APPROVED',
    makerCheckerId: 192837465,
    checker: {
        displayName: 'mockDisplayName'
    }
};

const mockEntityDenied = {
    state: 'DENIED',
    makerCheckerId: 192837465,
    checker: {
        displayName: 'mockDisplayName'
    }
};

const mockEntityNoCheckerDisplayName = {
    state: 'APPROVED',
    makerCheckerId: 192837465
};

const mockEntityOpen = {
    state: 'OPEN',
    makerCheckerId: 192837465,
    checker: {
        displayName: 'mockDisplayName'
    }
};

const mockActionFactoryData = 'mockActionFactoryData';
const mockTemplate = 'mockTemplate';

const isActionValid = (data) => {
    let valid = true;
    if(!data.hasOwnProperty('showActions')) {
        return false;
    }

    if(data.showActions === true && !data.hasOwnProperty('showButtons')) {
        return false;
    }

    if(!data.hasOwnProperty('isApproved') || !data.hasOwnProperty('userName')) {
        return false;
    }

    return true;
};

describe('Action Attachment Enricher', () => {
    let actionAttachmentEnricher;
    beforeAll(() => {
        actionFactory.mockReturnValue(mockActionFactoryData);
        attachmentActions.mockReturnValue(mockTemplate);
    });
    beforeEach(() => {
        actionFactory.mockClear();
        entityRegistry.updateEnricher.mockClear();
        attachmentActions.mockClear();
        subscribe.mockClear();
    });
    it('Should create a new action attachment enricher', () => {
        actionAttachmentEnricher = new ActionAttachmentEnricher();

        expect(MessageEnricher.mock.calls.length).toBe(1);
        expect(typeof actionAttachmentEnricher.enrich === 'function').toBe(true);
        expect(typeof actionAttachmentEnricher.action === 'function').toBe(true);
    });
    it('Should update enricher (approved attachment case)', () => {
        actionAttachmentEnricher.enrich(mockType, mockEntityApproved);

        expect(actionFactory.mock.calls.length).toBe(1);
        expect(actionFactory.mock.calls[0][0]).toEqual([]);
        expect(actionFactory.mock.calls[0][1]).toEqual('helpdesk-action-attachment-enricher');
        expect(actionFactory.mock.calls[0][2]).toEqual(mockEntityApproved);

        expect(subscribe.mock.calls.length).toBe(1);
        expect(subscribe.mock.calls[0][0]).toEqual('entity');

        expect(attachmentActions.mock.calls.length).toBe(1);
        expect(isActionValid(attachmentActions.mock.calls[0][0])).toBe(true);

        expect(entityRegistry.updateEnricher.mock.calls.length).toBe(1);
        expect(entityRegistry.updateEnricher.mock.calls[0][0]).toEqual(mockEntityApproved.makerCheckerId);
        expect(entityRegistry.updateEnricher.mock.calls[0][1]).toEqual(mockTemplate);
        expect(entityRegistry.updateEnricher.mock.calls[0][2]).toBe(mockActionFactoryData);
    });
    it('Should update enricher (denied attachment case)', () => {
        actionAttachmentEnricher.enrich(mockType, mockEntityDenied);

        expect(actionFactory.mock.calls.length).toBe(1);
        expect(actionFactory.mock.calls[0][0]).toEqual([]);
        expect(actionFactory.mock.calls[0][1]).toEqual('helpdesk-action-attachment-enricher');
        expect(actionFactory.mock.calls[0][2]).toEqual(mockEntityDenied);

        expect(subscribe.mock.calls.length).toBe(1);
        expect(subscribe.mock.calls[0][0]).toEqual('entity');

        expect(attachmentActions.mock.calls.length).toBe(1);
        expect(isActionValid(attachmentActions.mock.calls[0][0])).toBe(true);

        expect(entityRegistry.updateEnricher.mock.calls.length).toBe(1);
        expect(entityRegistry.updateEnricher.mock.calls[0][0]).toEqual(mockEntityDenied.makerCheckerId);
        expect(entityRegistry.updateEnricher.mock.calls[0][1]).toBe(mockTemplate);
        expect(entityRegistry.updateEnricher.mock.calls[0][2]).toBe(mockActionFactoryData);
    });
    it('Should update enricher (approved but no checker display name case)', () => {
        actionAttachmentEnricher.enrich(mockType, mockEntityNoCheckerDisplayName);

        expect(actionFactory.mock.calls.length).toBe(1);
        expect(actionFactory.mock.calls[0][0]).toEqual([]);
        expect(actionFactory.mock.calls[0][1]).toEqual('helpdesk-action-attachment-enricher');
        expect(actionFactory.mock.calls[0][2]).toEqual(mockEntityNoCheckerDisplayName);

        expect(subscribe.mock.calls.length).toBe(1);
        expect(subscribe.mock.calls[0][0]).toEqual('entity');

        expect(attachmentActions.mock.calls.length).toBe(1);
        expect(isActionValid(attachmentActions.mock.calls[0][0])).toBe(true);

        expect(entityRegistry.updateEnricher.mock.calls.length).toBe(1);
        expect(entityRegistry.updateEnricher.mock.calls[0][0]).toEqual(mockEntityNoCheckerDisplayName.makerCheckerId);
        expect(entityRegistry.updateEnricher.mock.calls[0][1]).toEqual(mockTemplate);
        expect(entityRegistry.updateEnricher.mock.calls[0][2]).toBe(mockActionFactoryData);
    });
    it('Should not update enricher (no action was performed case)', () => {
        actionAttachmentEnricher.enrich(mockType, mockEntityOpen);

        expect(actionFactory.mock.calls.length).toBe(0);
        expect(subscribe.mock.calls.length).toBe(0);
        expect(attachmentActions.mock.calls.length).toBe(0);
        expect(entityRegistry.updateEnricher.mock.calls.length).toBe(0);
    });
    it('Should do nothing (action function has no implementation)', () => {
        actionAttachmentEnricher.action();
    })
});