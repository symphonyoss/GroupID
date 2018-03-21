import { renderErrorMessage } from '../errorMessage';
import error from '../../templates/error.hbs';
import actionFactory from '../actionFactory';

jest.mock('../actionFactory');
jest.mock('../../templates/error.hbs');

const mockEntity = {
    ticketId: 'N3M19Z8SJ0'
};

const mockMessageError = "MessageError!";

const mockEnricherServiceName = "EnricherServiceName";

const expectedErrorMessage = {
    template: undefined,
    data: undefined,
    enricherInstanceId: mockEntity.ticketId
};

describe('Error Message', () => {
    error.mockClear();
    const errorMessage = renderErrorMessage(mockEntity, mockMessageError, mockEnricherServiceName);

    it('Should call \'actionFactory\' function with correct parameters', () => {
        expect(actionFactory.mock.calls.length).toBe(1);
        expect(actionFactory.mock.calls[0][0]).toEqual([]);
        expect(actionFactory.mock.calls[0][1]).toEqual(mockEnricherServiceName);
        expect(actionFactory.mock.calls[0][2]).toEqual(mockEntity);
    });
    it('Should call \'error\' template with correct parameters', () => {
        expect(error.mock.calls.length).toBe(1);
        expect(error.mock.calls[0][0].hasOwnProperty('message')).toBe(true);
    });
    it('Should render error message', () => {
        expect(errorMessage).toEqual(expectedErrorMessage);
    });
});

