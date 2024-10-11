# Weather Planner

## 1. Overview

The purpose of the application is to give recommendation for dressing and activities according to weather forecast for the next 12 hours after the invocation. Weather data is taken from an [external weather service](https://openweathermap.org/api) and AWS Gen AI makes the recommendation in real-time, in same web request.
This application invoke Generative AI service from Amazon Marketplace, [Bedrock](https://aws.amazon.com/bedrock/). After some analysis, the model chosen was: [Amazon Titan Text Premier](https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-titan.html), [InvokeModel](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/bedrock-runtime).
Weather data from the external server is included in Gen AI prompt. Part of weather data and Gen AI response are included in a web page according to below diagram.

![Weather Planning](aws_weather_planning_v1.png "Weather Planning")

### 1.1. Generative AI model selection
From the many Gen AI models in the Amazon AI Marketplace, I've considered Amazon Titan Text due to price and facilities that it has. A comparison among the Amazon Titan Text models (Premier, Express, Lite) lead to the conclusion that the models have the value/intelligence/features according to their [price](https://aws.amazon.com/bedrock/pricing/) and technical limitations of the moment, when LLMs are o a rising curve.
The trade-off between cost and value of the model is a common consideration when choosing between different tiers of language models from Amazon or other providers. If highly professional Gen AI response is crucial for your application, and if it's feasible within your constraints, using the Premier model might be the most reliable solution. My final choice was Premier due to the consistent responses that it provides in consecutive invocations.

## 2. Application functionality
Application will be available online as a web page in the Applications section of my website: [mihaiadam.com](https://mihaiadam.com/apps).<br>
The project is a Maven project that contains the Lambda function from the above architecture diagram. It can be built, but the deploy in AWS Cloud will not work as an application due to missing dependencies.<br>
The tests could ran in Eclipse due to Mockito data injections in the mocks of the AWS SDK clients, thus all the original code of the Lambda will run with mock data from real sources (API Gateway JSON event, weather API JSON response, Titan recommendation, S3 velocity templates).

## 3. AWS SDK features used
- The log is done in all application using Log4j2, that sends output to AWS CloudWatch service
- Each AWS client used is encapsulated in a POJO application service
- Deploy of AWS Lambda Function is done from the build process configured in pom.xml 
- Use Mockito & MockitoHamcrest test frameworks to mock some of the AWS services/clients; Thus, all the layers (handler,service,model) of the application, in their original form, are tested
- Use AspectJ and AWS Powertools-logging to trace the execution of methods in local and cloud environments
- Use Velocity framework templates, deployed in S3, to build Generative AI prompt and HTML page (src/main/resources)

