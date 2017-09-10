package com.ulfric.dragoon.curator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.ensemble.exhibitor.DefaultExhibitorRestClient;
import org.apache.curator.ensemble.exhibitor.ExhibitorEnsembleProvider;
import org.apache.curator.ensemble.exhibitor.ExhibitorRestClient;
import org.apache.curator.ensemble.exhibitor.Exhibitors;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.apache.curator.retry.RetryForever;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryOneTime;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.application.Container;
import com.ulfric.dragoon.cfg4j.Settings;
import com.ulfric.dragoon.curator.authentication.AclHelper;
import com.ulfric.dragoon.curator.authentication.VaultAuthentication;
import com.ulfric.dragoon.curator.config.ExhibitorConfig;
import com.ulfric.dragoon.curator.config.RetryConfig;
import com.ulfric.dragoon.curator.config.ZkConfig;
import com.ulfric.dragoon.extension.inject.Inject;

public class CuratorContainer extends Container {

	@Inject
	private ObjectFactory factory;

	@Inject(optional = true)
	private Namespace namespace;

	@Settings("zoo")
	private ZkConfig config;

	private CuratorFramework curator;

	public CuratorContainer() {
		addBootHook(this::register);
		addShutdownHook(this::unregister);
	}

	private void register() {
		curator = createCurator();
		bindToFactory();
	}

	private CuratorFramework createCurator() {
		CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();

		addNamespace(builder);
		addRetryPolicy(builder);
		addAuthenticationData(builder);
		addEnsembleData(builder);

		return builder.build();
	}

	private void addNamespace(CuratorFrameworkFactory.Builder builder) {
		if (namespace == null) {
			return;
		}

		builder.namespace(namespace.value());
	}

	private void bindToFactory() {
		factory.bind(CuratorFramework.class).toValue(curator);
	}

	private void addEnsembleData(CuratorFrameworkFactory.Builder builder) {
		EnsembleProvider provider;
		if (config.exhibitor().enabled()) {
			provider = getExhibitorEnsemble();
		} else {
			provider = getFixedEnsemble();
		}
		builder.ensembleProvider(provider);
	}

	private ExhibitorEnsembleProvider getExhibitorEnsemble() {
		ExhibitorConfig config = this.config.exhibitor();
		Exhibitors exhibitors = new Exhibitors(getConnections(), config.port(), config::backupConnection);
		ExhibitorRestClient client = new DefaultExhibitorRestClient(config.useSsl());

		RetryConfig retryConfig = config.retry().useDefault() ? this.config.retry() : config.retry();
		RetryPolicy retry = getRetryPolicy(retryConfig);

		return new ExhibitorEnsembleProvider(exhibitors, client, config.restUriPath(),
				config.pollingMs(), retry);
	}

	private FixedEnsembleProvider getFixedEnsemble() {
		return new FixedEnsembleProvider(getConnectionString());
	}

	private String getConnectionString() {
		return getConnections().stream().collect(Collectors.joining(","));
	}

	private List<String> getConnections() {
		List<String> connections = config.connections();
		if (connections.isEmpty()) {
			throw new IllegalStateException("connections must have at least one zookeeper instance");
		}
		return connections;
	}

	private void addRetryPolicy(CuratorFrameworkFactory.Builder builder) {
		builder.retryPolicy(getRetryPolicy(config.retry()));
	}

	private RetryPolicy getRetryPolicy(RetryConfig config) {
		int intervalMs = config.intervalMs();
		int maxRetries = config.maxRetries();

		if (config.forever() || isMax(maxRetries)) {
			return new RetryForever(intervalMs);
		}

		int maxIntervalMs = config.maxIntervalMs();

		if (isMax(maxIntervalMs)) {
			if (maxRetries == 1) {
				return new RetryOneTime(intervalMs);
			}

			return new RetryNTimes(maxRetries, intervalMs);
		}

		return new BoundedExponentialBackoffRetry(intervalMs, maxIntervalMs, maxRetries);
	}

	private boolean isMax(int value) {
		return value <= 0 || value == Integer.MAX_VALUE;
	}

	private void addAuthenticationData(CuratorFrameworkFactory.Builder builder) {
		if (config.authentication().enabled()) {
			String authInfo;
			if (config.authentication().useVault()) {
				authInfo = factory.request(VaultAuthentication.class).getAcl();
			} else {
				authInfo = config.authentication().acl();
			}

			builder.authorization(Collections.singletonList(AclHelper.parseAuthInfo(authInfo)));
		}
	}

	private void unregister() {
		curator.close();
		factory.bind(CuratorFramework.class).toNothing();
	}

}
