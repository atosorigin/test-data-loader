/**
 * MIT License
 *
 * Copyright (c) 2016 TRIOLOGY GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.triology.testdata.builder

class EntityBuilder {

    private Map<String, ?> entitiesByName = [:]
    private EntitiesScriptExecutor executor

    protected EntityBuilder(final EntitiesScriptExecutor executor) {
        this.executor = executor
    }

    /**
     * Creates an Instance of the specified  entityClass, registers it under the specified entityName and applies the
     * specified entityData definition
     *
     * @param entityClass - the type defining the entity
     * @param entityName - the name to reference the entity e.g. in another call to create
     * @param entityData - a Closure used to build the entity
     * @return the created entity
     */
    public <T> T create(@DelegatesTo.Target Class<T> entityClass, String entityName,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, genericTypeIndex = 0) Closure entityData = {}) {

        T entity = createEntityInstance(entityName, entityClass)

        def rehydrated = entityData.rehydrate(entity, this, this)
        rehydrated.resolveStrategy = Closure.DELEGATE_FIRST
        rehydrated.call()

        executor.fireEntityCreated(entityName, entity)
        return entity
    }

    public <T> T update(@DelegatesTo.Target Class<T> entityClass, String entityName,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, genericTypeIndex = 0) Closure entityData = {}) {

        T entity = entitiesByName[entityName]

        def rehydrated = entityData.rehydrate(entity, this, this)
        rehydrated.resolveStrategy = Closure.DELEGATE_FIRST
        rehydrated.call()

        executor.fireEntityCreated(entityName, entity)
        return entity
    }

    public <T> void delete(Class<T> entityClass, String entityName){
        T entity = entitiesByName[entityName]

        executor.fireEntityDeleted(entityName, entity)
    }

    public void commit(){
        executor.fireCommit()
    }

    private <T> T createEntityInstance(final String entityName, final Class<T> entityClass) {
        if (entitiesByName[entityName]) {
            throw new EntityBuilderException(
            "attempt to create an instance of $entityClass under the name of '$entityName' but an " +
            "entity with that name already exists: ${entitiesByName[entityName]}")
        }

        final T entity = entityClass.newInstance()
        entitiesByName[entityName] = entity
        return entity
    }

    /**
     * Implementation of Groovy's {@code propertyMissing} that returns the entity previously created under the property
     * name. This Method will be called during entity creation, when an entity is referenced.
     *
     * @param name String
     * @return a previously created entity
     */
    private def propertyMissing(final String name) {
        if (entitiesByName[name]) {
            return entitiesByName[name]
        }
        throw new EntityBuilderException("requested reference for entity with name '$name' cannot be resolved")
    }
}
