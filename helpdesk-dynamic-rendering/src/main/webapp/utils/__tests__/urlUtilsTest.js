import { getParameterByName } from "../urlUtils";

const mockValue = 'mockValue';
const mockParameter = 'mockParameter';
const mockUrl0 = `https://localhost:8100/helpdesk-renderer/controller.html?${mockParameter}=${mockValue}`;
const mockUrl1 = `https://localhost:8100/helpdesk-renderer/controller.html?param=mockparam&${mockParameter}=${mockValue}`;
const mockUrl2 = `https://localhost:8100/helpdesk-renderer/controller.html?param=mockparam`;
const mockUrl3 = `https://localhost:8100/helpdesk-renderer/controller.html?param=mockparam&${mockParameter}`;



describe('Url Utils', () => {
    it('Should find value in url with single matching parameter', () => {
        jsdom.reconfigure({
            url: mockUrl0
        });

        const value = getParameterByName(mockParameter);
        expect(value).toEqual(mockValue);
    });
    it('Should find value in url with multiple parameters, one matching', () => {
        jsdom.reconfigure({
            url: mockUrl1
        });

        const value = getParameterByName(mockParameter);
        expect(value).toEqual(mockValue);
    });
    it('Should not find value in url with no matching parameter', () => {
        jsdom.reconfigure({
            url: mockUrl2
        });

        const value = getParameterByName(mockParameter);
        expect(value).toBe(null);
    });
    it('Should not find value in url with no matching valid parameter', () => {
        jsdom.reconfigure({
            url: mockUrl3
        });

        const value = getParameterByName(mockParameter);
        expect(value).toBe(null);
    });
});