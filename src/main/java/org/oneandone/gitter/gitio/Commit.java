/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.oneandone.gitter.gitio;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author stephan
 */
public class Commit {
    @Getter @Setter
    private String authorName;
    @Getter @Setter
    private String authorEmail;
    @Getter @Setter
    private LocalDateTime when;
}
