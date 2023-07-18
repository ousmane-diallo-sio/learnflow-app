# Circle CI configuration file

version: 2.1  # CircleCI config version

jobs:  # Define the jobs section
  build-and-test:  # Define a job named "build"
    macos:
      xcode: 14.0.1

    working_directory: ~/LearnFlow/LearnFlow # Set the working directory to the root of the project
    steps:
      - checkout:  # Check out the code from the repository
          path: ~/LearnFlow

      - run:
          name: Setup Android SDK
          command: |
            export ANDROID_SDK_ROOT='/home/circleci/android-sdk'
            export JAVA_HOME='/usr/lib/jvm/java-8-openjdk-amd64'

      - run:
          name: Get current directory
          command: pwd

      - run:
          name: List files in current directory
          command: ls -la

      - run:
          name: Make gradlew executable
          command: chmod +x ./gradlew

      # Task : Download and install dependencies
      - run:
          name: Download Dependencies
          command: ./gradlew androidDependencies

      # Task : Build the project
      - run:
          name: Build
          command: ./gradlew clean assemble

      # Task : Run tests
      - run:
          name: Run Tests
          command: ./gradlew test

# Define a workflow named "simple-workflow" that includes the "build-and-test" job
workflows:
  simple-workflow:
    jobs:
      - build-and-test