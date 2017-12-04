### How to use the error in Banner. 

- Subscribe on error-banner/n

  const errorMessageService = SYMPHONY.services.subscribe('error-banner');
/n
- Set error on chat banner/n

  errorMessageService.setChatBanner(streamId, 'CHAT', 'Test Error', 'ERROR');

Implements on enrich the property contains streamId.

### How to use the error inline.

Create a template for errorInline, on Enrich you can pass the message:
  
  actions({ message: "Error Inline" })

Template:

<messageML>
  <span class="tempo-text-color--red">{{ message }}</span>
</messageML> 
