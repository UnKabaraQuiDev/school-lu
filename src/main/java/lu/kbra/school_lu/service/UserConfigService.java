package lu.kbra.school_lu.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.impl.DeferredDBTransaction;
import lu.kbra.school_lu.data.UserId;
import lu.kbra.school_lu.db.data.UserConfigData;
import lu.kbra.school_lu.db.table.UserConfigTable;

@Service
@RequiredArgsConstructor
public class UserConfigService {

	private final UserConfigTable userConfigTable;

	public Map<String, String> getConfig(final UserId id) {
		return this.userConfigTable.byUserId(id.id()).stream().collect(Collectors.toMap(UserConfigData::getKey, UserConfigData::getValue));
	}

	public void setConfig(final UserId id, final Map<String, String> map) {
		try (DeferredDBTransaction transaction = ((DeferredDatabase) this.userConfigTable.getDatabase()).createTransaction()) {
			final UserConfigTable userConfigProxy = transaction.use(this.userConfigTable);

			final List<UserConfigData> datas = userConfigProxy.byUserId(0);
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

}
