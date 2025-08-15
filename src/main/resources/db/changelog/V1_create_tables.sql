CREATE TABLE IF NOT EXISTS users (
	id UUID PRIMARY KEY,
	iam_id VARCHAR(255),
	profile_url VARCHAR(255),
	created_at TIMESTAMPTZ DEFAULT NOW(),
	updated_at TIMESTAMPTZ,
	deleted_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS entries (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    amount BIGINT NOT NULL,
    notes VARCHAR(255),
    photo_evidence VARCHAR(255),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS groups (
	id UUID PRIMARY KEY,
	group_name VARCHAR(255),
	combined_goal BIGINT,
	created_at TIMESTAMPTZ DEFAULT NOW(),
	updated_at TIMESTAMPTZ,
	deleted_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS user_entries (
	id UUID PRIMARY KEY,
	entry_id UUID NOT NULL,
	user_id UUID NOT NULL,
	group_id UUID,
	created_at TIMESTAMPTZ DEFAULT NOW(),
	updated_at TIMESTAMPTZ,
	deleted_at TIMESTAMPTZ,
	FOREIGN KEY (entry_id) REFERENCES entries(id),
	FOREIGN KEY (user_id) REFERENCES users(id),
	FOREIGN KEY (group_id) REFERENCES groups(id)
);

CREATE TABLE IF NOT EXISTS users_group (
	user_id UUID,
    group_id UUID,
    PRIMARY KEY (user_id, group_id),  -- This ensures a user can't be in the same group more than once
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (group_id) REFERENCES groups(id),
	created_at TIMESTAMPTZ DEFAULT NOW(),
	updated_at TIMESTAMPTZ,
	deleted_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS audit_log (
	id UUID PRIMARY KEY,
	actor VARCHAR(255),
	action VARCHAR(255),
	target VARCHAR(255),
	ipaddress VARCHAR(255),
	metadata JSONB
);