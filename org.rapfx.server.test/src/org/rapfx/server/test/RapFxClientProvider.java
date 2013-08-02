package org.rapfx.server.test;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rap.rwt.client.Client;
import org.eclipse.rap.rwt.client.service.ClientService;
import org.eclipse.rap.rwt.internal.client.ClientProvider;

@SuppressWarnings("restriction")
public class RapFxClientProvider implements ClientProvider {

	@Override
	public Client getClient() {
		return new Client() {
			private static final long serialVersionUID = 1L;

			@Override
			public <T extends ClientService> T getService(Class<T> type) {
				return null;
			}
		};
	}

	@Override
	public boolean accept(HttpServletRequest request) {
		return request.getHeader("User-Agent").contains("rapfx");
	}

}
