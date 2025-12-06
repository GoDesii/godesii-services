package com.godesii.godesii_services.exception;

import java.io.Serial;

public class ResourceNotFoundException extends GoDesiException
{
	@Serial
    private static final long serialVersionUID  = 1L;

	public ResourceNotFoundException(String message)
	{
		super(message);
	}

    public ResourceNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
