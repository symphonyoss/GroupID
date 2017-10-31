import { MessageEnricherBase } from 'symphony-integration-commons';

const enricherServiceName = 'helpdesk-enricher';
const messageEvents = [
  'com.symphony.bots.helpdesk.event.ticket.v2',
];

export default class HelpDeskBotEnricher extends MessageEnricherBase {
  constructor() {
    super(enricherServiceName, messageEvents);
  }

  enrich(type, entity) {
    const data = {
      claimTicket: {
        service: enricherServiceName,
        label: 'Claim IT',
        data: entity,
      },
    };

    const result = {
      template: `
        <messageML>
          <action id="claimTicket" class="tempo-btn tempo-btn--good"/>
        </messageML>
      `,
      data,
    };

    return result;
  }

  action(data) {
    const dialogTemplate = `
      <dialog>
          <h1>Hey</h1>        
      </dialog>
    `;

    this.dialogsService.show('action', 'issueRendered-renderer', dialogTemplate, {}, {});
    console.log("click");
  }
}
