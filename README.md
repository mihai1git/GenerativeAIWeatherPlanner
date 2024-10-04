# Weather Planner

## 1. Overview

The purpose of the application is to give recommendation for dressing and activities according to weather forecast for the next 12 hours after the invocation. Weather data is taken from an external weather service and AWS Gen AI makes the recommendation.
This application invoke Generative AI service from Amazon Marketplace, [Bedrock](https://aws.amazon.com/bedrock/). After some analysis, the model chosen was: [Amazon Titan Text Express](https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-titan.html), [InvokeModel](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/bedrock-runtime).
The application get weather data from an external live server and include it in the Gen AI prompt. Part of weather data and Gen AI response are included in a web page.

### 1.1. Generative AI model selection
From the many Gen AI models in the Amazon AI Marketplace, I've chosen Amazon Titan Text Express due to price and facilities that it has. It is a medium tier price and has the expected output. A comparison among the Amazon Titan Text models (Premier, Express, Lite) lead to the conclusion that the models have the value/intelligence/features according to their [price](https://aws.amazon.com/bedrock/pricing/) and technical limitations of the moment, when LLMs are o a rising curve.
The trade-off between cost and value of the model is a common consideration when choosing between different tiers of language models from Amazon or other providers. If highly professional Gen AI response is crucial for your application, and if it's feasible within your constraints, using the Premier model might be the most reliable solution. So, the Gen AI response displayed in the web page, is according to these. 

## 2. Application functionality


## 3. AWS SDK features used
- The log is done in all application using Log4j2, that sends output to AWS CloudWatch service
- Each AWS client used is encapsulated in a POJO application service
- Deploy of AWS Lambda Function is done from the build process configured in pom.xml 
- Use Mockito & MockitoHamcrest test frameworks to mock some of the AWS services/clients; Thus, all the layers (handler,service,model) of the application, in their original form, are tested
- Use AspectJ and AWS Powertools-logging to trace the execution of methods in local and cloud environments
- Use Velocity framework templates to build Generative AI prompt and HTML page (src/main/resources)
- Use HttpRequest to invoke external weather URL (https://openweathermap.org/api) and parse JSON response
