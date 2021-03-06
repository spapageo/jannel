/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Spyros Papageorgiou
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
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

package com.github.spapageo.jannel.msg;

/**
 * An administration message
 */
public class Admin implements Message {

    /**
     * The type of administration message command
     */
    private AdminCommand adminCommand;

    /**
     * The optional id of the box that send the command
     */
    private String boxId;

    /**
     * Default construction
     */
    public Admin() {
        this.adminCommand = AdminCommand.ADMIN_UNDEF;
    }

    /**
     * Construct a new admin command
     * @param adminCommand the type of admin command
     * @param boxId the box id associated with the command
     */
    public Admin(AdminCommand adminCommand, String boxId) {
        this.adminCommand = adminCommand;
        this.boxId = boxId;
    }

    @Override
    public MessageType getType() {
        return MessageType.ADMIN;
    }

    /**
     * @return the type of command
     */
    public AdminCommand getAdminCommand() {
        return adminCommand;
    }

    /**
     * Sets the new type of command
     * @param adminCommand the new command type
     */
    public void setAdminCommand(AdminCommand adminCommand) {
        this.adminCommand = adminCommand;
    }

    /**
     * @return the box id
     */
    public String getBoxId() {
        return boxId;
    }

    /**
     * Sets the new box id
     * @param boxId the new box id
     */
    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminCommand=" + adminCommand +
                ", boxId=" + boxId +
                '}';
    }
}
