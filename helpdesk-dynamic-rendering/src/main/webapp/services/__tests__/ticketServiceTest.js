import { claimTicket, getTicket, joinConversation } from '../../api/apiCalls';
import errorTypes from '../../utils/errorTypes';
import componentTypes from '../../utils/componentTypes';
import { getMessageByCode } from '../../errorMessages/messageByCode';

import TicketService from '../ticketService';

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
    services: {}
};

global.SYMPHONY.services = {
    subscribe: subscribe
};

const mockErrorCode = 101;

const mockErrorText = 'Intentional Error!';

const mockServiceName = 'mockServiceName';

const mockTicketUrl = 'mockurl.ticket/test/';

const mockError = new Promise(() => {
    throw { message: mockErrorCode }
});

const mockData = {
    entity: {
        streamId: 'mockStreamId'
    }
};

describe('Ticket Service', () => {
    let ticketService;
    describe('Successful Scenarios:', () => {
        beforeAll(() => {
            claimTicket.mockResolvedValue();
            joinConversation.mockResolvedValue();
        });
        it('Should create a new ticket service', () => {
            ticketService = new TicketService(mockServiceName);

            expect(ticketService.serviceName).toEqual(mockServiceName);
        });
        it('Should claim ticket', () => {
            ticketService.claim(mockData);

            expect(claimTicket.mock.calls.length).toBe(1);
            expect(claimTicket.mock.calls[0][0]).toEqual(mockData);
        });
        it('Should join conversation', () => {
            ticketService.join(mockData);

            expect(joinConversation.mock.calls.length).toBe(1);
            expect(joinConversation.mock.calls[0][0]).toEqual(mockData);
        });
        it('Should get ticket', () => {
            ticketService.getTicket(mockTicketUrl);

            expect(getTicket.mock.calls.length).toBe(1);
            expect(getTicket.mock.calls[0][0]).toEqual(mockTicketUrl);
        });
    });
    describe('Failing Scenarios:', () => {
        beforeAll(() => {
            claimTicket.mockResolvedValue(mockError);
            joinConversation.mockResolvedValue(mockError);
            getMessageByCode.mockReturnValue(mockErrorText);
        });
        beforeEach(() => {
            errorMessageService.setChatBanner.mockClear();
            getMessageByCode.mockClear();
        });
        it('Should fail to claim ticket', async () => {
            await ticketService.claim(mockData);

            expect(getMessageByCode.mock.calls.length).toBe(1);
            expect(getMessageByCode.mock.calls[0][0]).toEqual(mockErrorCode);

            expect(errorMessageService.setChatBanner.mock.calls.length).toBe(1);
            expect(errorMessageService.setChatBanner.mock.calls[0][0]).toEqual(mockData.entity.streamId);
            expect(errorMessageService.setChatBanner.mock.calls[0][1]).toEqual(componentTypes.CHAT);
            expect(errorMessageService.setChatBanner.mock.calls[0][2]).toEqual(mockErrorText);
            expect(errorMessageService.setChatBanner.mock.calls[0][3]).toEqual(errorTypes.ERROR);
        });
        it('Should fail to join conversation', async () => {
            await ticketService.join(mockData);

            expect(getMessageByCode.mock.calls.length).toBe(1);
            expect(getMessageByCode.mock.calls[0][0]).toEqual(mockErrorCode);

            expect(errorMessageService.setChatBanner.mock.calls.length).toBe(1);
            expect(errorMessageService.setChatBanner.mock.calls[0][0]).toEqual(mockData.entity.streamId);
            expect(errorMessageService.setChatBanner.mock.calls[0][1]).toEqual(componentTypes.CHAT);
            expect(errorMessageService.setChatBanner.mock.calls[0][2]).toEqual(mockErrorText);
            expect(errorMessageService.setChatBanner.mock.calls[0][3]).toEqual(errorTypes.ERROR);
        });
    });
});