# gherkin

![Build Status](https://travis-ci.org/podnov/gherkin.svg?branch=master)

Some quick gherkin stuff. A parser and a formatter. The formatter currently only formats data tables as that's the most annoying thing for me to manually manage. The formatter can be invoked using the gherking-formatter-maven-plugin.

### Use Case:

```

Feature: Formatting data tables

As a feature file maintainer
When I make changes to gherkin data tables
Then I want an automated process to reformat columns to the appropriate sizes


    Given these modified scenarios:

        Scenario: Employees Scenario 1

            Given these people:
            | firstName | lastName | employerOrganizationName                      |
            | Evan | Zeimet | CDW |
            | Larry | Page | Google |

        Scenario: Employees Scenario 2

            Given these people:
            | firstName | lastName | employerOrganizationName |
            | Judith | Faulkner | Epic |
            | Jeff | Bezos | Amazon |
            | Mark | Zuckerberg | Facebook |
            | Pete | Mitchell | U.S. Navy |
            | Nick | Bradshaw | U.S. Navy |
            | Tom | Kazanski | U.S. Navy |
            | Mike | Metcalf | U.S. Navy |

        Scenario: Organizations Scenario 1

            Given these organizations:
            | name      | address1                  | address2 | city          | state | zip   | yearFounded | active | dateCreated         |
            | CDW       | 200 N. Milwaukee Ave.     |          | Vernon Hills  | IL    | 60061 | 1984        | true   | 2015-10-09T00:00:00 |
            | Google    | 1600 Amphitheatre Parkway        |          | Mountain View | CA    | 94043 | 1998        | true   | 2015-10-10T00:00:00 |
            | Epic      | 1979 Milky Way            |          | Verona        | WI    | 53593 | 1979        | true   | 2015-10-11T00:00:00 |
            | Pets.com  |                           |          | San Francisco | CA    | 94101 | 1998        | false  | 2015-10-12T00:00:00 |

        Scenario: organizations Scenario 2

            Given these organizations:
            | name      | address1         | address2 | city       | state | zip   | yearFounded | active | dateCreated         |
            | Amazon    | 410 Terry Ave. N |          | Seattle    | WA    | 98109 | 1994        | true   | 2015-10-13T00:00:00 |
            | Facebook  | 1 Hacker Way     |     | Menlo Park | CA    | 94025 | 2004        | true   | 2015-10-14T00:00:00 |
            | U.S. Navy | The Pentagon     |          | Washington | DC    | 20001 | 1775        | true   | 2015-10-15T00:00:00 |

    When I run the gherkin formatter
    Then the scenarios should be formatted like so:

        Scenario: Employees Scenario 1

            Given these people:
            | firstName | lastName   | employerOrganizationName |
            | Evan      | Zeimet     | CDW                      |
            | Larry     | Page       | Google                   |

        Scenario: Employees Scenario 2

            Given these people:
            | firstName | lastName   | employerOrganizationName |
            | Judith    | Faulkner   | Epic                     |
            | Jeff      | Bezos      | Amazon                   |
            | Mark      | Zuckerberg | Facebook                 |
            | Pete      | Mitchell   | U.S. Navy                |
            | Nick      | Bradshaw   | U.S. Navy                |
            | Tom       | Kazanski   | U.S. Navy                |
            | Mike      | Metcalf    | U.S. Navy                |

        Scenario: Organizations Scenario 1

            Given these organizations:
            | name      | address1                  | address2 | city          | state | zip   | yearFounded | active | dateCreated         |
            | CDW       | 200 N. Milwaukee Ave.     |          | Vernon Hills  | IL    | 60061 | 1984        | true   | 2015-10-09T00:00:00 |
            | Google    | 1600 Amphitheatre Parkway |          | Mountain View | CA    | 94043 | 1998        | true   | 2015-10-10T00:00:00 |
            | Epic      | 1979 Milky Way            |          | Verona        | WI    | 53593 | 1979        | true   | 2015-10-11T00:00:00 |
            | Pets.com  |                           |          | San Francisco | CA    | 94101 | 1998        | false  | 2015-10-12T00:00:00 |

        Scenario: organizations Scenario 2

            Given these organizations:
            | name      | address1                  | address2 | city          | state | zip   | yearFounded | active | dateCreated         |
            | Amazon    | 410 Terry Ave. N          |          | Seattle       | WA    | 98109 | 1994        | true   | 2015-10-13T00:00:00 |
            | Facebook  | 1 Hacker Way              |          | Menlo Park    | CA    | 94025 | 2004        | true   | 2015-10-14T00:00:00 |
            | U.S. Navy | The Pentagon              |          | Washington    | DC    | 20001 | 1775        | true   | 2015-10-15T00:00:00 |


```

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