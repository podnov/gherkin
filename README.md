# gherkin

![Build Status](https://travis-ci.org/podnov/gherkin.svg?branch=master)

Some quick gherkin stuff. A parser and a formatter. The formatter currently only formats data tables as that's the most annoying thing for me to manually manage. The formatter can be invoked using the gherking-formatter-maven-plugin.

### Example Usage:

```

<plugin>
	<groupId>com.evanzeimet.gherkin</groupId>
	<artifactId>gherkin-formatter-maven-plugin</artifactId>
	<version>0.0.3</version>
	<executions>
		<execution>
			<phase>process-test-resources</phase>
			<goals>
				<goal>format-features</goal>
			</goals>
		</execution>
	</executions>
</plugin>

```