---
name: ticket
description: Implement a ticket based on it's description and acceptance criteria
---

Invocation: `/ticket <PLAN>` (e.g. `/ticket ID-777-do-something.md`).

### Action Algorithm
- Find a plan file based on a <PLAN> input
    - false: create it yourself under plans location configured for the project
- Check if [Original Ticket] section available
    - false: ask user to post the text and fill it yourself in the <PLAN> file
- Read [Plan] section
    - false: ask user if /grill-me skill needed for the ticket
        - true: Spawn a subagent and invoke /grill-me on <PLAN> and append a plan to a <PLAN> file under [Plan] section
        - false: Spawn a subagent and create an implementation plan and append a plan to a <PLAN> file under [Plan] section
- Delegate work to an agent to implement plan under [Plan] section:
    - "backend-architect" if it is a BE / DevOps related change
    - "frontend-developer" if it is a FE related change
    - "backend-unit-test-writer" for unit tests in BE
- Run "thermo-nuclear-code-quality-review" skill after implementation is done
    - review results and delegate back to implementation agent if fix needed
    - iterate up to 3 times per task
- Delegate to "reality-checker" to check all changes works as expected and there are no regressions
