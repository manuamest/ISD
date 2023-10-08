-- ---------------------------------------------------------------------------
-- Model
-- ---------------------------------------------------------------------------

DROP TABLE Reply;
DROP TABLE Event;

-- --------------------------------- Event ------------------------------------

CREATE TABLE Event ( eventID BIGINT NOT NULL AUTO_INCREMENT,
                        eventName VARCHAR(255) COLLATE latin1_bin NOT NULL,
                        eventDescription VARCHAR(255) COLLATE latin1_bin  NOT NULL,
                        eventDate DATETIME NOT NULL,
                        duration INTEGER not null,
                        state BIT,
                        acceptedReplies INTEGER,
                        deniedReplies INTEGER,
                        creationDate DATETIME NOT NULL,
                        CONSTRAINT EventPK PRIMARY KEY(eventID),
                        CONSTRAINT validDuration CHECK ( duration >= 0 )
                        /*CONSTRAINT validCreation CHECK ( creationDate < eventDate)*/) ENGINE = InnoDB;

-- --------------------------------- Reply ------------------------------------

CREATE TABLE Reply ( replyID BIGINT NOT NULL AUTO_INCREMENT,
                     userEmail VARCHAR(100) COLLATE latin1_bin NOT NULL,
                     eventID BIGINT NOT NULL,
                     replyDate DATE,
                     state BIT,
                     CONSTRAINT ReplyPK PRIMARY KEY(replyID),
                     CONSTRAINT ReplyEventIDFK FOREIGN KEY(eventID)
                         REFERENCES Event(eventID) ON DELETE CASCADE ) ENGINE = InnoDB;