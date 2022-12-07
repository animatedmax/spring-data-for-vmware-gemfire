/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.mapping;

import org.springframework.data.gemfire.mapping.model.GemfireSimpleTypeHolder;
import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.MutablePersistentEntity;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.TypeInformation;

/**
 * Spring Data {@link AbstractMappingContext} implementation defining entity mapping meta-data
 * for GemFire persistent entities.
 *
 * @author Oliver Gierke
 * @author John Blum
 * @see org.springframework.data.mapping.context.AbstractMappingContext
 */
public class GemfireMappingContext extends AbstractMappingContext<GemfirePersistentEntity<?>, GemfirePersistentProperty> {

	/**
	 * Constructs a GemfireMappingContext with a GemfireSimpleTypeHolder.
	 *
	 * @see org.springframework.data.gemfire.mapping.model.GemfireSimpleTypeHolder
	 */
	public GemfireMappingContext() {
		// Technically, the following call is not Thread-safe (the "this" reference escapes), but then MappingContext
		// makes no Thread-safety guarantees, even though, most likely, and especially in GemFire's case,
		// the MappingContext will be used in a highly concurrent context (modeled after SD MongoDB for consistency)!
		setSimpleTypeHolder(new GemfireSimpleTypeHolder());
	}

	/**
	 * @inheritDoc
	 * @see org.springframework.data.mapping.context.AbstractMappingContext#createPersistentEntity(org.springframework.data.util.TypeInformation)
	 */
	@Override
	protected <T> GemfirePersistentEntity<T> createPersistentEntity(TypeInformation<T> typeInformation) {
		return new GemfirePersistentEntity<>(typeInformation);
	}

	/**
	 * @inheritDoc
	 * @see org.springframework.data.mapping.context.AbstractMappingContext#createPersistentProperty(Property, MutablePersistentEntity, SimpleTypeHolder)
	 */
	@Override
	protected GemfirePersistentProperty createPersistentProperty(Property property, GemfirePersistentEntity<?> owner,
			SimpleTypeHolder simpleTypeHolder) {

		return new GemfirePersistentProperty(property, owner, simpleTypeHolder);
	}
}
