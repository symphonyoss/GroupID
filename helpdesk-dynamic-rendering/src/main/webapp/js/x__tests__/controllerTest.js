import ClaimTicketEnricher from '../../enrichers/claimTicketEnricher';
import ActionClaimTicketEnricher from '../../enrichers/actionClaimTicketEnricher';
import AttachmentEnricher from '../../enrichers/attachmentEnricher';
import ActionAttachmentEnricher from '../../enrichers/actionAttachmentEnricher';

const getParameterByName = jest.fn().mockImplementation((id) => {
    return 'mock_id';
});
jest.mock('../../enrichers/claimTicketEnricher');
jest.mock('../../enrichers/actionClaimTicketEnricher');
jest.mock('../../enrichers/attachmentEnricher');
jest.mock('../../enrichers/actionAttachmentEnricher');

beforeEach(() => {
    getParameterByName.mockClear();
    ClaimTicketEnricher.mockClear();
    ActionClaimTicketEnricher.mockClear();
    AttachmentEnricher.mockClear();
    ActionAttachmentEnricher.mockClear();
});
