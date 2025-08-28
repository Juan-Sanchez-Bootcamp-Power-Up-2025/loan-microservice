-- LOAN TYPE
CREATE TABLE IF NOT EXISTS loan_types (
  id VARCHAR(20) PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  min_amount NUMERIC(15) NOT NULL,
  max_amount NUMERIC(15) NOT NULL ,
  interest_rate NUMERIC(3) NOT NULL,
  validation BOOLEAN NOT NULL
);

-- LOAN STATUS
CREATE TABLE IF NOT EXISTS loan_statuses (
  id VARCHAR(20) PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  description VARCHAR(120) NOT NULL
);

-- LOAN APPLICATION
CREATE TABLE IF NOT EXISTS loan_applications (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  amount NUMERIC(15) NOT NULL,
  term NUMERIC(5) NOT NULL,
  email VARCHAR(50) NOT NULL ,
  status VARCHAR(20) NOT NULL,
  type VARCHAR(20) NOT NULL,
  document_id VARCHAR(20) NOT NULL,
  CONSTRAINT fk_loan_application_loan_status FOREIGN KEY (status) REFERENCES loan_statuses(id),
  CONSTRAINT fk_loan_application_loan_type FOREIGN KEY (type) REFERENCES loan_types(id)
);

INSERT INTO loan_types (id, name, min_amount, max_amount, interest_rate, validation)
VALUES
('LOW', 'Low loan', 500.00, 5000.00, 8.50, TRUE),
('HIGH', 'High loan', 5001.00, 50000.00, 12.75, FALSE);

INSERT INTO loan_statuses (id, name, description)
VALUES
('PENDING', 'Pending revision', 'The loan application was submitted.');
