import { MessageEnricherBase } from 'symphony-integration-commons';

const enricherServiceName = 'helpdesk-enricher';
const messageEvents = [
  'com.symphony.bots.helpdesk.event.ticket',
];

export default class HelpDeskBotEnricher extends MessageEnricherBase {
  constructor() {
    super(enricherServiceName, messageEvents);
  }

  enrich(type, entity) {
    const data = {
      claimTicket: {
        service: enricherServiceName,
        label: 'Claim',
        data: entity,
      },
    };

    const result = {
      template: `
        <messageML>
          <action id="claimTicket" class="tempo-text-color--link"/>
        </messageML>
      `,
      data,
    };

    return result;
  }

  action(data) {
    console.log(data);
  }
}
