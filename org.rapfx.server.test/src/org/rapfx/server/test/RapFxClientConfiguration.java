package org.rapfx.server.test;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.internal.application.ApplicationImpl;
import org.eclipse.rap.ui.internal.servlet.WorkbenchApplicationConfigurator;

@SuppressWarnings("restriction")
public class RapFxClientConfiguration extends WorkbenchApplicationConfigurator {

	@Override
	public void configure(Application application) {
		super.configure(application);

		((ApplicationImpl) application)
				.addClientProvider(new RapFxClientProvider());
	}

}
