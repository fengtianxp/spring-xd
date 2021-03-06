/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.xd.dirt.module;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.xd.module.ModuleDefinition;


/**
 * A {@link ModuleRegistry} that delegates to several ModuleRegistries, in order.
 * 
 * @author Eric Bottard
 */
public class CompositeModuleRegistry implements ModuleRegistry {

	private final ModuleRegistry[] delegates;

	/**
	 * @param cp
	 * @param file
	 */
	public CompositeModuleRegistry(ModuleRegistry... delegates) {
		this.delegates = delegates;
	}

	@Override
	public ModuleDefinition lookup(String name, String type) {
		for (ModuleRegistry delegate : delegates) {
			ModuleDefinition result = delegate.lookup(name, type);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@Override
	public List<ModuleDefinition> findDefinitions(String name) {
		Set<String> alreadySeen = new HashSet<String>();
		List<ModuleDefinition> result = new ArrayList<ModuleDefinition>();
		for (ModuleRegistry delegate : delegates) {
			List<ModuleDefinition> sub = delegate.findDefinitions(name);
			for (ModuleDefinition definition : sub) {
				// First registry's module shadows subsequent
				if (alreadySeen.add(makeKeyFor(definition))) {
					result.add(definition);
				}
			}
		}
		return result;
	}

	private String makeKeyFor(ModuleDefinition definition) {
		return definition.getType() + "|" + definition.getName();
	}

}
