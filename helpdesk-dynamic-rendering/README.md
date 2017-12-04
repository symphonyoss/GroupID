### How to set an error message at Symphony's error banner. 

- Subscribe to error-banner service:

  const errorMessageService = SYMPHONY.services.subscribe('error-banner');

- Set the error message:

  errorMessageService.setChatBanner(streamId, 'CHAT', 'This is an error message', 'ERROR');

The streamId must be retrieved in the enricher.

### How to use an inline error message.

Create an error template and pass its message through the enricher using a template engine (e.g., Handlebars):
  
  actions({ message: "This is an error message" })

Template:

`<messageML>
  <span class="tempo-text-color--red">{{ message }}</span>
</messageML>`
