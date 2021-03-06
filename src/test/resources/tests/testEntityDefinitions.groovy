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
import java.text.SimpleDateFormat

import de.triology.testdata.loader.testentities.AnotherTestEntity
import de.triology.testdata.loader.testentities.BasicTestEntity
import de.triology.testdata.loader.testentities.TestEntityWithToManyRelationship
import de.triology.testdata.loader.testentities.TestEntityWithToOneRelationship

create BasicTestEntity, 'basicEntity', {
    stringProperty = 'a string value'
    integerProperty = 5
    dateProperty = new SimpleDateFormat('dd.MM.yyyy').parse('18.11.2015')
}

create BasicTestEntity, 'secondBasicEntity'

create AnotherTestEntity, 'entityOfAnotherClass'

create TestEntityWithToOneRelationship, 'entityWithToOneRelationship', {
    referencedEntity = create BasicTestEntity, 'referencedInstance',  {
        stringProperty = 'string in referenced entity'
        integerProperty = 222
    }
}

create TestEntityWithToOneRelationship, 'deeplyNestedEntities', {
    testEntityWithToOneRelationship = create TestEntityWithToOneRelationship, 'nest1', {
        testEntityWithToOneRelationship = create TestEntityWithToOneRelationship, 'nest2', {
            referencedEntity = create BasicTestEntity, 'nest3', {
                stringProperty = 'deeply nested string'
            }
        }
    }
}

create TestEntityWithToOneRelationship, 'entityReferencingPreviouslyCreatedEntity', {
    referencedEntity = secondBasicEntity
}

create TestEntityWithToManyRelationship, 'entityWithCollection', {
    toManyRelationship = [
            create(BasicTestEntity, 'createdInPlace', {
                integerProperty = 5
            }),
            basicEntity
    ]
}