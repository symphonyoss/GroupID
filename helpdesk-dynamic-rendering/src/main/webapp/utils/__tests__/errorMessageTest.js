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
    beforeEach(() => {
        error.mockClear();
    });
    it('Should render error message', () => {
        const errorMessage = renderErrorMessage(mockEntity, mockMessageError, mockEnricherServiceName);

        expect(actionFactory.mock.calls.length).toBe(1);
        expect(actionFactory.mock.calls[0][0]).toEqual([]);
        expect(actionFactory.mock.calls[0][1]).toEqual(mockEnricherServiceName);
        expect(actionFactory.mock.calls[0][2]).toEqual(mockEntity);

        expect(error.mock.calls.length).toBe(1);
        expect(error.mock.calls[0][0].hasOwnProperty('message')).toBe(true);

        expect(errorMessage).toEqual(expectedErrorMessage);
    });
});

