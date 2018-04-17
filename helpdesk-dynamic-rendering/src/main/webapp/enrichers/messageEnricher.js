export default class MessageEnricher {
  constructor(name, messageEvents, application) {
    this._name = name;
    this.messageEvents = messageEvents;
    this.application = application;
    this.implements = ['enrich', 'action'];
  }

  get name() {
    return this._name;
  }

  init() {
    SYMPHONY.services.make(this._name, this, this.implements, true);
  }

  register() {
    const entity = SYMPHONY.services.subscribe('entity');

    this.messageEvents.forEach((messageEvent) => {
      entity.registerRendererEnricher(messageEvent, {}, this._name, this.application);
    });
  }
}
