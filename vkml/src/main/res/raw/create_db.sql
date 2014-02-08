CREATE TABLE audio (
    _id INTEGER PRIMARY KEY,
    aid INTEGER NOT NULL,
    artist TEXT,
    title TEXT,
    genre TEXT,
    ext_url TEXT,
    loc_uri TEXT,
    uptime INTEGER,
    status TEXT,
    duration INTEGER
);