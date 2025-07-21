-- Sample data initialization for H2 database

-- Companies
INSERT INTO companies (id, name, created_at, updated_at, is_active) VALUES
(1, 'TechCorp Inc.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
(2, 'InnovateLabs', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
(3, 'StartupX', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false);

-- Users
INSERT INTO users (id, username, password, email, first_name, last_name, created_at, updated_at, is_active, roles, company_id) VALUES
(1, 'john.doe', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9wBc3FDjPkJA5uS', 'john.doe@techcorp.com', 'John', 'Doe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 'MANAGER', 1),
(2, 'jane.smith', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9wBc3FDjPkJA5uS', 'jane.smith@techcorp.com', 'Jane', 'Smith', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 'DEVELOPER', 1),
(3, 'mike.wilson', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9wBc3FDjPkJA5uS', 'mike.wilson@innovatelabs.com', 'Mike', 'Wilson', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 'ARCHITECT', 2),
(4, 'sarah.brown', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9wBc3FDjPkJA5uS', 'sarah.brown@innovatelabs.com', 'Sarah', 'Brown', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 'DEVELOPER', 2);

-- Meetings
INSERT INTO meetings (id, title, host_user_id, company_id, start_time, end_time, status, created_at, updated_at) VALUES
(1, 'Sprint Planning Meeting', 1, 1, '2024-02-01 10:00:00', '2024-02-01 11:30:00', 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Architecture Review', 3, 2, '2024-02-02 14:00:00', '2024-02-02 15:00:00', 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Weekly Standup', 1, 1, '2024-02-05 09:00:00', NULL, 'SCHEDULED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Participants
INSERT INTO participants (id, user_id, meeting_id, joined_at, left_at) VALUES
(1, 1, 1, '2024-02-01 10:00:00', '2024-02-01 11:30:00'),
(2, 2, 1, '2024-02-01 10:02:00', '2024-02-01 11:30:00'),
(3, 3, 2, '2024-02-02 14:00:00', '2024-02-02 15:00:00'),
(4, 4, 2, '2024-02-02 14:05:00', '2024-02-02 15:00:00'),
(5, 1, 3, NULL, NULL),
(6, 2, 3, NULL, NULL);

-- Tickets
INSERT INTO tickets (id, title, github_repository_id, user_id, company_id, status, is_closed, created_at, updated_at) VALUES
(1, 'Implement user authentication', 'techcorp/auth-service', 2, 1, 'IN_PROGRESS', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Fix database connection issues', 'techcorp/backend', 2, 1, 'OPEN', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Design new API endpoints', 'innovatelabs/api-gateway', 4, 2, 'REVIEW', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Action Items
INSERT INTO action_items (id, title, ticket_id, meeting_id, is_confirmed, assignee_user_id, created_at, updated_at) VALUES
(1, 'Create user authentication flow', 1, 1, true, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Review database connection pooling', 2, 1, false, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Document API specification', 3, 2, true, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Set up monitoring for new endpoints', NULL, 2, false, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Voice Profiles
INSERT INTO voice_profiles (id, user_id, voice_characteristics, created_at, updated_at) VALUES
(1, 1, 'Deep voice, confident speaking pace, clear pronunciation', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 'Medium pitch, fast speaking pace, slight accent', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 3, 'Low voice, measured speaking pace, technical vocabulary', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 4, 'Higher pitch, enthusiastic tone, clear articulation', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);