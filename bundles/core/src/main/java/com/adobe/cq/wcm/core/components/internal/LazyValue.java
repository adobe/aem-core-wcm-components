/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2023 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.wcm.core.components.internal;

import java.util.Optional;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Resolve a value lazily.
 * 
 * In SlingModels, not all values computed in the @PostConstruct method are always
 * used. That means that there might be values which are only consumed in certain 
 * circumstances, but which have overhead to calculate. In case this value is not used
 * at all, this overhead is wasted time.
 * 
 * Wrapping them into the LazyValue type will do the calculation of the value only
 * when required.
 * 
 * 
 *
 * @param <T> the type of the value
 */

public class LazyValue<T> {

	private Optional<T> value;
	private Supplier<T> supplier;
	
	/**
	 * 
	 * @param supplier to provide the value
	 */
	public LazyValue(@NotNull Supplier<T> supplier) {
		if (supplier == null) {
			throw new IllegalArgumentException("supplier must not be null");
		}
		this.supplier = supplier;
	}
	
	@Nullable
	public T get() {
		if (value == null) {
			value = Optional.ofNullable(supplier.get());
		}
		return value.orElse(null);
	}
	
	
	
	
}
