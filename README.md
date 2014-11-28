TweetFeeder
===========

This project is developed in requirement for course Cloud Computing and Big Data at Columbia University.
Team:
Annapurna Vemuri (amv2164)
Sowmya Sree Bhagavatula (sb3636)
Madhuri S Palle (msp2167)
Sri Lakshmi Chintala (sc3772)

This a standalone project run as a daemon which performs the following tasks
1. Fetch twitter data using Twitter streaming API and persist to a database deployed in Amazon RDS
2. Add the twitter data to Amazon SQS queue
3. Launch worker threads which read from the SQS queue and perform sentiment analysis on the tweet using Alchemy API
4. Update the twitter data with sentiment value and publish to Amazon SNS

The data published to SNS is read from a servlet hosted at http://cloud-assgn2.elasticbeanstalk.com. The web application is developed as a separate project and the code for it is at https://github.com/AnnapurnaVemuri/TwitMap-1
