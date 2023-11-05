# TrelloClone
Kanban page

Motivation: Improvement over sticky notes on Whiteboard. Allows data-driven project staffing etc.
Track work on a project. We should be able to ask some basic questions:
- Are we making good sized tasks? How long does it take to finish a task?
- Do we have enough engineers? How long after a task is created is it moved to State=Doing?

One, default Project.
Minimal user table (John, Paul, George, Ringo)
Task = {State, AssignedTo, Description, Comment(s)}
States: Todo, Doing, Done
AssignedTo: user
Desciption: text
Comments: List<text>

HTTP reqs:
Create Task (State=Todo, user=null); save time of creation.
return taskid
Modify Task. // change assigned user(s); change state; add Comment; edit Description
return success/failure
Delete Task.
return success/failure
Show Board.
return List<tasks> (json).

