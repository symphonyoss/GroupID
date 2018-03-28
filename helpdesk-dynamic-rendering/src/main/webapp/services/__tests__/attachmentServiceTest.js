import { approveAttachment, denyAttachment, searchAttachment } from '../../api/apiCalls';
import errorTypes from '../../utils/errorTypes';
import componentTypes from '../../utils/componentTypes';
import { getMessageByCode } from '../../errorMessages/messageByCode';

import AttachmentService from '../attachmentService';

jest.mock('../../api/apiCalls');
jest.mock('../../utils/componentTypes');
jest.mock('../../utils/errorTypes');
jest.mock('../../errorMessages/messageByCode');

const errorMessageService = {
    setChatBanner: jest.fn()
};

const subscribe = jest.fn().mockImplementation((serviceName) => {
    if(serviceName === 'error-banner') {
        return errorMessageService;
    }
});

global.SYMPHONY = {
    services: {
        subscribe: subscribe
    }
};

const mockServiceName = 'mockServiceName';

const mockMessage = {
    userId: 123456789,
    entity: {
        approveUrl: 'mockurl.attachment/approve',
        denyUrl: 'mockurl.attachment/deny',
        streamId: 'streamIdLmaoThisIsAmazing'
    }
};

const mockAttachmentUrl = 'mockurl.attachment/test/'

const mockErrorText = 'Intentional Error!';

const mockErrorCode = 101;

const mockError = new Promise(() => {
    throw { message: mockErrorCode }
});

describe('Attachment Service', () => {
    let attachmentService;
    describe('Successful Scenarios:', () => {
        beforeAll(() => {
            approveAttachment.mockResolvedValue();
            denyAttachment.mockResolvedValue();
        });
        it('Should create a new attachment service', () => {
            attachmentService = new AttachmentService(mockServiceName);

            expect(attachmentService.serviceName).toEqual(mockServiceName);
        });
        it('Should approve an attachment', () => {
            attachmentService.approve(mockMessage);

            expect(approveAttachment.mock.calls.length).toBe(1);
            expect(approveAttachment.mock.calls[0][0]).toEqual(mockMessage.entity.approveUrl);
            expect(approveAttachment.mock.calls[0][1]).toEqual(mockMessage.userId);
        });
        it('Should deny an attachment', () => {
            attachmentService.deny(mockMessage);

            expect(denyAttachment.mock.calls.length).toBe(1);
            expect(denyAttachment.mock.calls[0][0]).toEqual(mockMessage.entity.denyUrl);
            expect(denyAttachment.mock.calls[0][1]).toEqual(mockMessage.userId);
        });
        it('Should search an attachment', () => {
            attachmentService.search(mockAttachmentUrl);

            expect(searchAttachment.mock.calls.length).toBe(1);
            expect(searchAttachment.mock.calls[0][0]).toEqual(mockAttachmentUrl);
        });
    });
    describe('Failing Scenarios:', () => {
        beforeAll(() => {
            approveAttachment.mockResolvedValue(mockError);
            denyAttachment.mockResolvedValue(mockError);
            getMessageByCode.mockReturnValue(mockErrorText);
        });
        beforeEach(() => {
            errorMessageService.setChatBanner.mockClear();
            getMessageByCode.mockClear();
        });
        it('Should fail to approve an attachment', async () => {
            await attachmentService.approve(mockMessage);

            expect(getMessageByCode.mock.calls.length).toBe(1);
            expect(getMessageByCode.mock.calls[0][0]).toEqual(mockErrorCode);

            expect(errorMessageService.setChatBanner.mock.calls.length).toBe(1);
            expect(errorMessageService.setChatBanner.mock.calls[0][0]).toEqual(mockMessage.entity.streamId);
            expect(errorMessageService.setChatBanner.mock.calls[0][1]).toEqual(componentTypes.CHAT);
            expect(errorMessageService.setChatBanner.mock.calls[0][2]).toEqual(mockErrorText);
            expect(errorMessageService.setChatBanner.mock.calls[0][3]).toEqual(errorTypes.ERROR);
        });
        it('Should fail to deny an attachment', async () => {
            await attachmentService.deny(mockMessage);

            expect(getMessageByCode.mock.calls.length).toBe(1);
            expect(getMessageByCode.mock.calls[0][0]).toEqual(mockErrorCode);

            expect(errorMessageService.setChatBanner.mock.calls.length).toBe(1);
            expect(errorMessageService.setChatBanner.mock.calls[0][0]).toEqual(mockMessage.entity.streamId);
            expect(errorMessageService.setChatBanner.mock.calls[0][1]).toEqual(componentTypes.CHAT);
            expect(errorMessageService.setChatBanner.mock.calls[0][2]).toEqual(mockErrorText);
            expect(errorMessageService.setChatBanner.mock.calls[0][3]).toEqual(errorTypes.ERROR);
        });
    });
});