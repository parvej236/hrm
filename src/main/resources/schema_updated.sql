create function hrm.get_next_value(p_name character varying) returns integer
    language plpgsql
as
$$
DECLARE
    current_val INTEGER;
BEGIN
    UPDATE hrm.sequences
    SET value = value + 1
    WHERE name = p_name
    RETURNING value INTO current_val;

    RETURN current_val;
END;
$$;

alter function hrm.get_next_value(varchar) owner to postgres;