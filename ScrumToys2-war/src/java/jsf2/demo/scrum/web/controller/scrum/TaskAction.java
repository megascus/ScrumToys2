/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.

Oracle and Java are registered trademarks of Oracle and/or its affiliates.
Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package jsf2.demo.scrum.web.controller.scrum;

import jsf2.demo.scrum.domain.task.Task;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import jsf2.demo.scrum.application.scrum_management.ScrumManager;
import jsf2.demo.scrum.domain.task.TaskRepository;
import jsf2.demo.scrum.domain.task.TaskStatus;
import jsf2.demo.scrum.infra.context.ViewScoped;
import jsf2.demo.scrum.infra.web.controller.BaseCrudAction;

/**
 * @author Dr. Spock (spock at dev.java.net)
 */
@ConversationScoped @Model
public class TaskAction extends BaseCrudAction<Long, Task> implements Serializable {

    private static final long serialVersionUID = 1L;

    //=========================================================================
    // Fields.
    //=========================================================================    
    
    @Inject
    TaskRepository taskRepository;
    
    @Inject
    ScrumManager scrumManager;

    //=========================================================================
    // Properties.
    //=========================================================================
    
    @Produces @Named @ViewScoped
    public List<Task> getTasks() {
        if (scrumManager.getCurrentStory() != null) {
            return scrumManager.getCurrentStory().getTasks();
        } else {
            return Collections.emptyList();
        }
    }

    @Produces @Named @ApplicationScoped
    public List<TaskStatus> getStatuses() {
        return Arrays.asList(TaskStatus.values());
    }    
    
    //=========================================================================
    // Actions.
    //=========================================================================
    
    @Override
    protected void onSelectCurrentEntity(Task task) {
        scrumManager.setCurrentTask(task);
    }
    
    @Override
    protected Task doCreate() {
        return new Task();
    }

    @Override
    protected void doSave() {
        scrumManager.saveCurrentTask();
    }

    @Override
    protected void doRemove(Task task) {
        scrumManager.removeTask(task);
    }

    public String showStories() {
        // Reset selected story and task to null.
        scrumManager.setCurrentSprint(scrumManager.getCurrentSprint());
        
        return redirectTo("/story/show");
    }
    
    //=========================================================================
    // Validator.
    //=========================================================================
    
    public void checkUniqueTaskName(FacesContext context, UIComponent component, Object newValue) {
        final String newName = (String) newValue;

        long count = taskRepository.countOtherTasksWithName(scrumManager.getCurrentStory(), scrumManager.getCurrentTask(), newName);
        if (count > 0) {
            throw new ValidatorException(getFacesMessageForKey("task.form.label.name.unique"));
        }
    }
}