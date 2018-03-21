import messages from '../messages';
import { getMessageByCode } from '../messageByCode';

jest.mock('../messages');

const messageByCode = {
    400: messages.INVALID_REQUEST_ERROR,
    401: messages.UNAUTHORIZED_ERROR,
    404: messages.PERFORM_ACTION_ERROR,
    500: messages.GENERIC_ERROR,
};

describe('Error Messages', () => {
    describe('Get Message by Code', () => {
        it('Should return INVALID_REQUEST_ERROR (400)', () => {
            expect(getMessageByCode(400)).toBe(messageByCode[400]);
        });

        it('Should return UNAUTHORIZED_ERROR (401)', () => {
            expect(getMessageByCode(401)).toBe(messageByCode[401]);
        });

        it('Should return PERFORM_ACTION_ERROR (404)', () => {
            expect(getMessageByCode(404)).toBe(messageByCode[404]);
        });

        it('Should return GENERIC_ERROR (500)', () => {
            expect(getMessageByCode(1)).toBe(messageByCode[500]);
            expect(getMessageByCode(10)).toBe(messageByCode[500]);
            expect(getMessageByCode(200)).toBe(messageByCode[500]);
            expect(getMessageByCode(100)).toBe(messageByCode[500]);
            expect(getMessageByCode(500)).toBe(messageByCode[500]);
        });
    });
});