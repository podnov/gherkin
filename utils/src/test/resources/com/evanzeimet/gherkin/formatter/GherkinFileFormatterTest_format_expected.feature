Feature: Do awesome

In order to do awesome
As a software engineer
I want to be able to dev quickly

Background:
	Given the following people exist:
	| id | firstName | lastName   |
	| 1  | Evan      | Zeimet     |
	| 2  | John      | Boone      |
	| 3  | Tom       | Pendergast |

	And the following offices exist:
	| id | name                   | address 1             | address 2              | city         | state | zip   | main phone number |
	| 1  | Corporate Headquarters | 200 N. Milwaukee Ave. |                        | Vernon Hills | IL    | 60061 | 847.465.6000      |
	| 2  | Wisconsin - Madison    | 5520 Research Park Dr |                        | Fitchburg    | WI    | 53711 | 1-(847)-465-6000  |
	# some data table comment
	| 3  | Wisconsin - Milwaukee  | N19w23993 Ridgeview   | Parkway West Suite 120 | Waukesha     | WI    | 53188 | 262.521.5600      |


Scenario: Search for potential offices for person 1
	When I search for potential offices for "Evan Zeimet"
	# I'm anding because x
	And there's a chained When
	Then I should see these offices:
	| id | name                   | address 1             | address 2              | city         | state | zip   | main phone number |
	| 2  | Wisconsin - Madison    | 5520 Research Park Dr |                        | Fitchburg    | WI    | 53711 | 1-(847)-465-6000  |
	| 3  | Wisconsin - Milwaukee  | N19w23993 Ridgeview   | Parkway West Suite 120 | Waukesha     | WI    | 53188 | 262.521.5600      |
	And there's a chained Then

Scenario: Search for potential offices for person 2
	When I search for potential offices for "John Boone"
	Then I should see these offices:
	| id | name                   | address 1             | address 2              | city         | state | zip   | main phone number |
	| 1  | Corporate Headquarters | 200 N. Milwaukee Ave. |                        | Vernon Hills | IL    | 60061 | 847.465.6000      |
	| 2  | Wisconsin - Madison    | 5520 Research Park Dr |                        | Fitchburg    | WI    | 53711 | 1-(847)-465-6000  |

	And there's some other table:
	| other column 1 | other column 2 |
	| other value 1  | other value 2  |