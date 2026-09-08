package lu.kbra.school_lu.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lu.kbra.pclib.db.impl.DeferredDBTransaction;
import lu.kbra.school_lu.db.data.UserConfigData;
import lu.kbra.school_lu.db.data.UserData;
import lu.kbra.school_lu.db.table.UserConfigTable;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserConfigService {

	private final UserConfigTable userConfigTable;

	public Map<String, String> getConfig(final UserData userData) {
		return this.userConfigTable.byUser(userData).stream().collect(Collectors.toMap(UserConfigData::getKey, UserConfigData::getValue));
	}

	public String getConfig(final UserData userData, final String key) {
		final UserConfigData data = this.userConfigTable.byUserAndKey(userData, key);
		return data == null ? null : data.getValue();
	}

	public void setConfig(final UserData userData, final Map<String, String> map) {
		try (DeferredDBTransaction transaction = this.userConfigTable.getDatabase().createTransaction()) {
			final UserConfigTable userConfigProxy = transaction.use(this.userConfigTable);

			final List<UserConfigData> datas = userConfigProxy.byUser(userData);
			final List<UserConfigData> toKeep = new ArrayList<>();
			datas.removeIf(c -> {
				if (map.containsKey(c.getKey())) {
					c.setValue(map.get(c.getKey()));
					toKeep.add(c);
					return true;
				}

				return false;
			});
			userConfigProxy.deleteAll(datas);
			userConfigProxy.updateAll(toKeep);

			transaction.commit();
		}
	}

	public void setConfig(final UserData userData, final String key, final String value) {
		final UserConfigData data = this.userConfigTable.byUserAndKey(userData, key);
		if (data != null) {
			if (value == null) {
				this.userConfigTable.delete(data);
			} else {
				data.setValue(value);
				this.userConfigTable.update(data);
			}
		} else {
			this.userConfigTable.insert(new UserConfigData(userData.getId(), key, value));
		}
	}

}
