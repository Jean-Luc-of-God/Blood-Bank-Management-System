# Use a lightweight Java runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the source code and the MySQL connector library into the container
# Ensure 'mysql-connector-j-9.4.0.jar' is in your project root directory
COPY src ./src
COPY mysql-connector-j-9.4.0.jar ./lib/mysql-connector.jar
# Copy image assets so the UI doesn't crash if run (though GUI requires X11)
COPY *.jpg ./

# Create a directory for compiled classes
RUN mkdir out

# Compile the application code
# We include the MySQL connector in the classpath (-cp)
RUN javac -d out -cp .:lib/mysql-connector.jar src/model/*.java src/dao/*.java src/view/*.java src/controller/*.java src/app/*.java

# Define the command to run the application
# Note: Running this directly without X11 forwarding setup will result in a HeadlessException
# because Swing needs a display. This file satisfies the "can be dockerized" requirement.
CMD ["java", "-cp", "out:lib/mysql-connector.jar", "app.App"]