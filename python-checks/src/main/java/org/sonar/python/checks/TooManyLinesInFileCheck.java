/*
 * SonarQube Python Plugin
 * Copyright (C) 2011-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.python.checks;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Grammar;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.checks.SquidCheck;

@Rule(
    key = TooManyLinesInFileCheck.CHECK_KEY,
    priority = Priority.MAJOR,
    name = "Files should not have too many lines",
    tags = Tags.BRAIN_OVERLOAD
)
@SqaleConstantRemediation("1h")
@ActivatedByDefault
public class TooManyLinesInFileCheck extends SquidCheck<Grammar> {
  public static final String CHECK_KEY = "S104";
  private static final int DEFAULT = 1000;

  @RuleProperty(
    key = "maximum",
    defaultValue = "" + DEFAULT)
  public int maximum = DEFAULT;

  @Override
  public void init() {
    subscribeTo(GenericTokenType.EOF);
  }

  @Override
  public void visitNode(AstNode astNode) {
    int lines = astNode.getTokenLine();

    if (lines > maximum) {
      String message = "File \"{0}\" has {1} lines, which is greater than {2} authorized. Split it into smaller files.";
      getContext().createFileViolation(this, message, getContext().getFile().getName(), lines, maximum);
    }
  }
}

