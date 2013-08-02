package org.rapfx.server.test;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.internal.application.ApplicationImpl;
import org.eclipse.rap.ui.internal.servlet.WorkbenchApplicationConfigurator;

@SuppressWarnings("restriction")
public class RapFxClientConfiguration implements ApplicationConfiguration {

	@Override
	public void configure(Application application) {
		WorkbenchApplicationConfigurator conf = new WorkbenchApplicationConfigurator(
				null);
		conf.configure(application);
		// application.setOperationMode(OperationMode.SWT_COMPATIBILITY);

		((ApplicationImpl) application)
				.addClientProvider(new RapFxClientProvider());
		application.addEntryPoint("/thin", ThinDemoEntry.class, null);
	}

}
