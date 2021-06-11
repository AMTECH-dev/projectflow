insert into pf.employee(id, name, email, phone, position, is_fired)
values (1, 'Кирилл', 'some-email@example.com', '89992410182', 'DIRECTOR', false),
       (2, 'Кирилл', 'some-email@example.com', '89992410182', 'DIRECTOR', false),
       (3, 'Кирилл', 'some-email@example.com', '89992410182', 'DIRECTOR', false);

insert into pf.direction(id, lead_id, name)
values (1, 1, 'Direction 01'),
       (2, 2, 'Direction 02');

insert into pf.project(id, name, project_lead_id, direction_id, description, create_date, status)
values (1, 'Project 01', 1, 1, 'Description 01', '2021-06-09T11:49:03.839234Z', 'UNAPPROVED'),
       (2, 'Project 01', 2, 1, 'Description 02', '2021-06-09T11:49:03.839234Z', 'UNAPPROVED'),
       (3, 'Project 01', 3, 2, 'Description 02', '2021-06-09T11:49:03.839234Z', 'UNAPPROVED');

insert
into pf.project_comment(project_id, message, create_date, login)
values
    (1, 'Comment 01', '2021-06-09T11:49:03.839234Z', 'KiraLis39'),
    (2, 'Comment 02', '2021-06-08T09:49:13.839234Z', 'KiraLis47'),
    (1, 'Comment 03', '2021-06-07T11:00:03.839234Z', 'KiraLis39'),
    (3, 'Comment 04', '2021-06-08T11:30:03.839234Z', 'OKiraLis58'),
    (3, 'Comment 05', '2021-06-10T11:21:03.839234Z', 'KiraLis62');